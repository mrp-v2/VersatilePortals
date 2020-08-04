package mrp_v2.randomdimensions.world;

import mrp_v2.randomdimensions.block.PortalBlock;
import mrp_v2.randomdimensions.common.capabilities.IPortalDataCapability;
import mrp_v2.randomdimensions.util.ObjectHolder;
import mrp_v2.randomdimensions.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern.PatternHelper;
import net.minecraft.block.pattern.BlockPattern.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Teleporter implements ITeleporter {

	protected final ServerWorld world;
	protected final Random random;

	public Teleporter(ServerWorld worldIn) {
		this.world = worldIn;
		this.random = new Random(worldIn.getSeed());
	}

	@Override
	public Entity placeEntity(Entity entityIn, ServerWorld currentWorld, ServerWorld destinationWorld, float yaw,
			Function<Boolean, Entity> repositionEntity) {
		IPortalDataCapability portalData = Util.getPortalData(entityIn);
		String worldID = Util.getWorldID(
				currentWorld.func_234923_W_() != World.field_234918_g_ ? currentWorld : destinationWorld);
		Vector3d lastPortalVec = portalData.getLastPortalVec(worldID);
		Direction teleportDirection = portalData.getTeleportDirection(worldID);
		double newPosX = entityIn.getPosX();
		double newPosZ = entityIn.getPosZ();
		boolean isOriginalWorldShrinked = currentWorld.func_230315_m_().func_236045_g_();
		boolean isDestinationWorldShrinked = destinationWorld.func_230315_m_().func_236045_g_();
		if (!isOriginalWorldShrinked && isDestinationWorldShrinked) {
			newPosX /= 8.0D;
			newPosZ /= 8.0D;
		} else if (isOriginalWorldShrinked && !isDestinationWorldShrinked) {
			newPosX *= 8.0D;
			newPosZ *= 8.0D;
		}
		double minNewPosX = Math.min(-2.9999872E7D, destinationWorld.getWorldBorder().minX() + 16.0D);
		double minNewPosZ = Math.min(-2.9999872E7D, destinationWorld.getWorldBorder().minZ() + 16.0D);
		double maxNewPosX = Math.min(2.9999872E7D, destinationWorld.getWorldBorder().maxX() - 16.0D);
		double maxNewPosZ = Math.min(2.9999872E7D, destinationWorld.getWorldBorder().maxZ() - 16.0D);
		newPosX = MathHelper.clamp(newPosX, minNewPosX, maxNewPosX);
		newPosZ = MathHelper.clamp(newPosZ, minNewPosZ, maxNewPosZ);
		if (entityIn instanceof ServerPlayerEntity) {
			double newPosY = entityIn.getPosY();
			currentWorld.getProfiler().startSection("moving");
			entityIn.setLocationAndAngles(newPosX, newPosY, newPosZ, entityIn.rotationYaw, entityIn.rotationPitch);
			currentWorld.getProfiler().endSection();
			currentWorld.getProfiler().startSection("placing");
			entityIn.setLocationAndAngles(newPosX, newPosY, newPosZ, entityIn.rotationYaw, entityIn.rotationPitch);
			portalData.setRemainingPortalCooldown(entityIn.getPortalCooldown());
			if (!this.placeInPortal(entityIn, entityIn.rotationYaw, lastPortalVec, teleportDirection)) {
				if (this.world.func_234923_W_() != World.field_234918_g_) {
					this.makePortal(entityIn);
					this.placeInPortal(entityIn, entityIn.rotationYaw, lastPortalVec, teleportDirection);
				} else {
					entityIn.detach();
					ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entityIn;
					serverPlayer.getServerWorld().removePlayer(serverPlayer, true);
					if (!serverPlayer.queuedEndExit) {
						serverPlayer.queuedEndExit = true;
						serverPlayer.connection
								.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241768_e_, 0.0F));
						return entityIn;
					}
				}
			}
			currentWorld.getProfiler().endSection();
			entityIn.setWorld(destinationWorld);
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entityIn;
			destinationWorld.addDuringPortalTeleport(serverPlayerEntity);
			serverPlayerEntity.connection.setPlayerLocation(entityIn.getPosX(), entityIn.getPosY(),
					entityIn.getPosZ(), entityIn.rotationYaw, entityIn.rotationPitch);
			return entityIn;
		}
		Vector3d entityInMotionVec = entityIn.getMotion();
		float rotationModifier;
		BlockPos blockpos;
		blockpos = new BlockPos(newPosX, entityIn.getPosY(), newPosZ);
		PortalInfo portalInfo = this.placeInExistingPortal(blockpos, entityInMotionVec, teleportDirection,
				lastPortalVec.x, lastPortalVec.y);
		if (portalInfo == null) {
			return null;
		}
		portalData.setRemainingPortalCooldown(entityIn.getPortalCooldown());
		blockpos = new BlockPos(portalInfo.pos);
		entityInMotionVec = portalInfo.motion;
		rotationModifier = portalInfo.rotation;
		currentWorld.getProfiler().endStartSection("reloading");
		Entity newEntity = entityIn.getType().create(destinationWorld);
		if (newEntity != null) {
			newEntity.copyDataFromOld(entityIn);
			newEntity.moveToBlockPosAndAngles(blockpos, newEntity.rotationYaw + rotationModifier,
					newEntity.rotationPitch);
			newEntity.setMotion(entityInMotionVec);
			destinationWorld.addFromAnotherDimension(newEntity);
			if (destinationWorld.func_234923_W_() == World.field_234920_i_) {
				ServerWorld.func_241121_a_(destinationWorld);
			}
		}
		return newEntity;
	}

	public boolean placeInPortal(Entity entity, float f, Vector3d lastPortalVec, Direction teleportDirection) {
		PortalInfo portalinfo = this.placeInExistingPortal(entity.getPosition(),
				entity.getMotion(), teleportDirection, lastPortalVec.x, lastPortalVec.y);
		if (portalinfo == null) {
			return false;
		}
		Vector3d portalInfoPos = portalinfo.pos;
		Vector3d portalInfoMotion = portalinfo.motion;
		entity.setMotion(portalInfoMotion);
		entity.rotationYaw = f + portalinfo.rotation;
		entity.moveForced(portalInfoPos.x, portalInfoPos.y, portalInfoPos.z);
		return true;
	}

	@Nullable
	public PortalInfo placeInExistingPortal(BlockPos pos, Vector3d vec3d,
			Direction directionIn, double d1, double d2) {
		PointOfInterestManager pointOfInterestManager = this.world.getPointOfInterestManager();
		pointOfInterestManager.ensureLoadedAndValid(this.world, pos, 128);
		List<PointOfInterest> pointOfInterestList = pointOfInterestManager.getInSquare((pointOfInterestType) -> pointOfInterestType == ObjectHolder.PORTAL_POINT_OF_INTEREST_TYPE, pos, 128, PointOfInterestManager.Status.ANY).collect(Collectors.toList());
		Optional<PointOfInterest> optionalPointOfInterest = pointOfInterestList.stream()
				.min(Comparator.<PointOfInterest>comparingDouble((pointOfInterest) -> pointOfInterest.getPos().distanceSq(pos)).thenComparingInt((pointOfInterest) -> pointOfInterest.getPos().getY()));
		return optionalPointOfInterest.map((pointOfInterest) -> {
			BlockPos pointOfInterestPos = pointOfInterest.getPos();
			this.world.getChunkProvider().registerTicket(TicketType.PORTAL, new ChunkPos(pointOfInterestPos), 3,
					pointOfInterestPos);
			PatternHelper patternHelper = PortalBlock.createPatternHelper(this.world,
					pointOfInterestPos);
			return patternHelper.getPortalInfo(directionIn, pointOfInterestPos, d2, vec3d,
					d1);
		}).orElse(null);
	}

	public boolean makePortal(Entity entityIn) {
		double d0 = -1.0D;
		int j = MathHelper.floor(entityIn.getPosX());
		int k = MathHelper.floor(entityIn.getPosY());
		int l = MathHelper.floor(entityIn.getPosZ());
		int i1 = j;
		int j1 = k;
		int k1 = l;
		int l1 = 0;
		int i2 = this.random.nextInt(4);
		BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
		for (int j2 = j - 16; j2 <= j + 16; ++j2) {
			double d1 = j2 + 0.5D - entityIn.getPosX();
			for (int l2 = l - 16; l2 <= l + 16; ++l2) {
				double d2 = l2 + 0.5D - entityIn.getPosZ();
				loop1:
				for (int j3 = this.world.func_234938_ad_() - 1; j3 >= 0; --j3) {
					if (this.world.isAirBlock(mutableBlockPos.setPos(j2, j3, l2))) {
						while (j3 > 0 && this.world.isAirBlock(mutableBlockPos.setPos(j2, j3 - 1, l2))) {
							--j3;
						}
						for (int k3 = i2; k3 < i2 + 4; ++k3) {
							int l3 = k3 % 2;
							int i4 = 1 - l3;
							if (k3 % 4 >= 2) {
								l3 = -l3;
								i4 = -i4;
							}
							for (int j4 = 0; j4 < 3; ++j4) {
								for (int k4 = 0; k4 < 4; ++k4) {
									for (int l4 = -1; l4 < 4; ++l4) {
										int i5 = j2 + (k4 - 1) * l3 + j4 * i4;
										int j5 = j3 + l4;
										int k5 = l2 + (k4 - 1) * i4 - j4 * l3;
										mutableBlockPos.setPos(i5, j5, k5);
										if (l4 < 0
												&& !this.world.getBlockState(mutableBlockPos).getMaterial().isSolid()
												|| l4 >= 0 && !this.world.isAirBlock(mutableBlockPos)) {
											continue loop1;
										}
									}
								}
							}
							double d5 = j3 + 0.5D - entityIn.getPosY();
							double d7 = d1 * d1 + d5 * d5 + d2 * d2;
							if (d0 < 0.0D || d7 < d0) {
								d0 = d7;
								i1 = j2;
								j1 = j3;
								k1 = l2;
								l1 = k3 % 4;
							}
						}
					}
				}
			}
		}
		if (d0 < 0.0D) {
			for (int l5 = j - 16; l5 <= j + 16; ++l5) {
				double d3 = l5 + 0.5D - entityIn.getPosX();
				for (int j6 = l - 16; j6 <= l + 16; ++j6) {
					double d4 = j6 + 0.5D - entityIn.getPosZ();
					loop2:
					for (int i7 = this.world.func_234938_ad_() - 1; i7 >= 0; --i7) {
						if (this.world.isAirBlock(mutableBlockPos.setPos(l5, i7, j6))) {
							while (i7 > 0 && this.world.isAirBlock(mutableBlockPos.setPos(l5, i7 - 1, j6))) {
								--i7;
							}
							for (int l7 = i2; l7 < i2 + 2; ++l7) {
								int l8 = l7 % 2;
								int k9 = 1 - l8;

								for (int i10 = 0; i10 < 4; ++i10) {
									for (int k10 = -1; k10 < 4; ++k10) {
										int i11 = l5 + (i10 - 1) * l8;
										int j11 = i7 + k10;
										int k11 = j6 + (i10 - 1) * k9;
										mutableBlockPos.setPos(i11, j11, k11);
										if (k10 < 0
												&& !this.world.getBlockState(mutableBlockPos).getMaterial().isSolid()
												|| k10 >= 0 && !this.world.isAirBlock(mutableBlockPos)) {
											continue loop2;
										}
									}
								}
								double d6 = i7 + 0.5D - entityIn.getPosY();
								double d8 = d3 * d3 + d6 * d6 + d4 * d4;
								if (d0 < 0.0D || d8 < d0) {
									d0 = d8;
									i1 = l5;
									j1 = i7;
									k1 = j6;
									l1 = l7 % 2;
								}
							}
						}
					}
				}
			}
		}
		int i6 = i1;
		int k2 = j1;
		int k6 = k1;
		int l6 = l1 % 2;
		int i3 = 1 - l6;
		if (l1 % 4 >= 2) {
			l6 = -l6;
			i3 = -i3;
		}
		if (d0 < 0.0D) {
			j1 = MathHelper.clamp(j1, 70, this.world.func_234938_ad_() - 10);
			k2 = j1;
			for (int j7 = -1; j7 <= 1; ++j7) {
				for (int i8 = 1; i8 < 3; ++i8) {
					for (int i9 = -1; i9 < 3; ++i9) {
						int l9 = i6 + (i8 - 1) * l6 + j7 * i3;
						int j10 = k2 + i9;
						int l10 = k6 + (i8 - 1) * i3 - j7 * l6;
						boolean flag = i9 < 0;
						mutableBlockPos.setPos(l9, j10, l10);
						this.world.setBlockState(mutableBlockPos,
								flag ? ObjectHolder.INDESTRUCTIBLE_PORTAL_FRAME_BLOCK.getDefaultState()
										: Blocks.AIR.getDefaultState());
					}
				}
			}
		}
		for (int k7 = -1; k7 < 3; ++k7) {
			for (int j8 = -1; j8 < 4; ++j8) {
				if (k7 == -1 || k7 == 2 || j8 == -1 || j8 == 3) {
					mutableBlockPos.setPos(i6 + k7 * l6, k2 + j8, k6 + k7 * i3);
					this.world.setBlockState(mutableBlockPos,
							ObjectHolder.INDESTRUCTIBLE_PORTAL_FRAME_BLOCK.getDefaultState(), 3);
				}
			}
		}
		BlockState blockstate = ObjectHolder.INDESTRUCTIBLE_PORTAL_BLOCK.getDefaultState().with(
				BlockStateProperties.HORIZONTAL_AXIS,
				l6 == 0 ? Direction.Axis.Z : Direction.Axis.X);
		for (int k8 = 0; k8 < 2; ++k8) {
			for (int j9 = 0; j9 < 3; ++j9) {
				mutableBlockPos.setPos(i6 + k8 * l6, k2 + j9, k6 + k8 * i3);
				this.world.setBlockState(mutableBlockPos, blockstate, 18);
			}
		}
		return true;
	}
}
