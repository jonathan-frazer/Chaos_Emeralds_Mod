package net.sonicrushxii.chaos_emerald.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.EmeraldType;
import net.sonicrushxii.chaos_emerald.entities.master_emerald.EmeraldTransformer;
import net.sonicrushxii.chaos_emerald.modded.ModEntityTypes;
import net.sonicrushxii.chaos_emerald.modded.ModSounds;
import org.jetbrains.annotations.Nullable;

public class MasterEmeraldBlock extends Block implements SimpleWaterloggedBlock {

    public static final VoxelShape SHAPE = Block.box(0,0,0,16,18,16);

    public static void spawnEmeraldTransformer(BlockPos emeraldPos, Level pLevel ,EmeraldType emeraldType)
    {
        if(pLevel.isClientSide) return;

        // Create the Emerald Transformer
        EmeraldTransformer entity = new EmeraldTransformer(ModEntityTypes.EMERALD_TRANSFORMER.get(), pLevel);
        entity.setPos(
                emeraldPos.getX()+0.5,
                emeraldPos.getY()+0.5,
                emeraldPos.getZ()+0.5
        );                                                                   // Set position
        entity.setDuration(72);                                              // Set duration
        entity.setTransformType((byte) emeraldType.ordinal());               // Set transformType

        // Add the entity to the world
        pLevel.addFreshEntity(entity);
    }

    public MasterEmeraldBlock(Properties pProperties)
    {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pHand == InteractionHand.MAIN_HAND && !pLevel.isClientSide) {
            //Playsound
            pLevel.playSound(pPlayer,pPos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5f,1.1f);

            //Find Correct Rotation
            Rotation correctRot = null;
            BlockPos stoneBrickOffset = new BlockPos(0,-1,-1);
            for(Rotation pRotation : Rotation.values())
            {
                BlockPos stoneBrickPos = pPos.offset(stoneBrickOffset.rotate(pRotation));
                BlockState retrievedBlockState = pLevel.getBlockState(stoneBrickPos);

                //If a Brick is found, or Is facing North, Select that as the desired Rotation
                if("minecraft:stone_bricks".equals(ForgeRegistries.BLOCKS.getKey(retrievedBlockState.getBlock())+"")) {
                    correctRot = pRotation;
                    break;
                }
            }
            if(correctRot == null) correctRot = Rotation.COUNTERCLOCKWISE_90;

            //Calculate the Offset based on the Rotation
            BlockPos aquaEmeraldOffset      =    new BlockPos(-6,0,0).rotate(correctRot);
            BlockPos blueEmeraldOffset      =    new BlockPos(0,0,-6).rotate(correctRot);
            BlockPos greenEmeraldOffset     =   new BlockPos(3,0,5).rotate(correctRot);
            BlockPos greyEmeraldOffset      =    new BlockPos(0,0,6).rotate(correctRot);
            BlockPos purpleEmeraldOffset    =  new BlockPos(-3,0,5).rotate(correctRot);
            BlockPos redEmeraldOffset       =     new BlockPos(3,0,-5).rotate(correctRot);
            BlockPos yellowEmeraldOffset    =  new BlockPos(-3,0,-5).rotate(correctRot);

            //Apply Offset to the Current Pos
            BlockPos aquaEmeraldPos     =   pPos.offset(aquaEmeraldOffset);
            BlockPos blueEmeraldPos     =   pPos.offset(blueEmeraldOffset);
            BlockPos greenEmeraldPos    =   pPos.offset(greenEmeraldOffset);
            BlockPos greyEmeraldPos     =   pPos.offset(greyEmeraldOffset);
            BlockPos purpleEmeraldPos   =   pPos.offset(purpleEmeraldOffset);
            BlockPos redEmeraldPos      =   pPos.offset(redEmeraldOffset);
            BlockPos yellowEmeraldPos   =   pPos.offset(yellowEmeraldOffset);

            pPlayer.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {

                //Trips if even a single Emerald is transforming
                byte transformCount = 0;

                //Aqua Emerald
                if(chaosEmeraldCap.hasAllManuscripts() || chaosEmeraldCap.hasManuscript((byte)EmeraldType.AQUA_EMERALD.ordinal()))
                {
                    BlockState retrievedBlockState = pLevel.getBlockState(aquaEmeraldPos);
                    if("chaos_emerald:chaos_emerald/aqua_emerald"
                            .equals(ForgeRegistries.BLOCKS.getKey(retrievedBlockState.getBlock())+""))
                    //Transformer
                    {
                        transformCount++;
                        spawnEmeraldTransformer(aquaEmeraldPos, pLevel, EmeraldType.AQUA_EMERALD);
                    }
                }

                //Blue Emerald
                if(chaosEmeraldCap.hasAllManuscripts() || chaosEmeraldCap.hasManuscript((byte)EmeraldType.BLUE_EMERALD.ordinal()))
                {
                    BlockState retrievedBlockState = pLevel.getBlockState(blueEmeraldPos);
                    if("chaos_emerald:chaos_emerald/blue_emerald"
                            .equals(ForgeRegistries.BLOCKS.getKey(retrievedBlockState.getBlock())+""))
                    //Transformer
                    {
                        transformCount++;
                        spawnEmeraldTransformer(blueEmeraldPos,pLevel,EmeraldType.BLUE_EMERALD);
                    }
                }

                //Green Emerald
                if(chaosEmeraldCap.hasAllManuscripts() || chaosEmeraldCap.hasManuscript((byte)EmeraldType.GREEN_EMERALD.ordinal()))
                {
                    BlockState retrievedBlockState = pLevel.getBlockState(greenEmeraldPos);
                    if("chaos_emerald:chaos_emerald/green_emerald"
                            .equals(ForgeRegistries.BLOCKS.getKey(retrievedBlockState.getBlock())+""))
                    //Transformer
                    {
                        transformCount++;
                        spawnEmeraldTransformer(greenEmeraldPos,pLevel,EmeraldType.GREEN_EMERALD);
                    }

                }

                //Grey Emerald
                if(chaosEmeraldCap.hasAllManuscripts() || chaosEmeraldCap.hasManuscript((byte)EmeraldType.GREY_EMERALD.ordinal()))
                {
                    BlockState retrievedBlockState = pLevel.getBlockState(greyEmeraldPos);
                    if("chaos_emerald:chaos_emerald/grey_emerald"
                            .equals(ForgeRegistries.BLOCKS.getKey(retrievedBlockState.getBlock())+""))
                    //Transformer
                    {
                        transformCount++;
                        spawnEmeraldTransformer(greyEmeraldPos, pLevel, EmeraldType.GREY_EMERALD);
                    }
                }

                //Purple Emerald
                if(chaosEmeraldCap.hasAllManuscripts() || chaosEmeraldCap.hasManuscript((byte)EmeraldType.PURPLE_EMERALD.ordinal()))
                {
                    BlockState retrievedBlockState = pLevel.getBlockState(purpleEmeraldPos);
                    if("chaos_emerald:chaos_emerald/purple_emerald"
                            .equals(ForgeRegistries.BLOCKS.getKey(retrievedBlockState.getBlock())+""))
                    //Transformer
                    {
                        transformCount++;
                        spawnEmeraldTransformer(purpleEmeraldPos, pLevel, EmeraldType.PURPLE_EMERALD);
                    }
                }

                //Red Emerald
                if(chaosEmeraldCap.hasAllManuscripts() || chaosEmeraldCap.hasManuscript((byte)EmeraldType.RED_EMERALD.ordinal()))
                {
                    BlockState retrievedBlockState = pLevel.getBlockState(redEmeraldPos);
                    if("chaos_emerald:chaos_emerald/red_emerald"
                            .equals(ForgeRegistries.BLOCKS.getKey(retrievedBlockState.getBlock())+""))
                    //Transformer
                    {
                        transformCount++;
                        spawnEmeraldTransformer(redEmeraldPos,pLevel,EmeraldType.RED_EMERALD);
                    }

                }

                //Yellow Emerald
                if(chaosEmeraldCap.hasAllManuscripts() || chaosEmeraldCap.hasManuscript((byte)EmeraldType.YELLOW_EMERALD.ordinal()))
                {
                    BlockState retrievedBlockState = pLevel.getBlockState(yellowEmeraldPos);
                    if("chaos_emerald:chaos_emerald/yellow_emerald"
                            .equals(ForgeRegistries.BLOCKS.getKey(retrievedBlockState.getBlock())+""))
                    //Transformer
                    {
                        transformCount++;
                        spawnEmeraldTransformer(yellowEmeraldPos, pLevel, EmeraldType.YELLOW_EMERALD);
                    }
                }

                //Play Sound
                if(transformCount > 0)
                {
                    pLevel.playSound(null,
                            pPos.getX()+0.5,pPos.getY()+0.5,pPos.getZ()+0.5,
                            ModSounds.SUPER_EMERALD_TRANSFORM.get(),
                            SoundSource.MASTER, 0.75f, 1.0f);
                }
            });
        }

        return InteractionResult.FAIL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        boolean isWater = pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, isWater);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if(pPlacer instanceof ServerPlayer player){
            player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.defaultFluidState() : super.getFluidState(pState);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {

        if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        if (entity instanceof EnderMan) {
            return false;
        }
        return super.canEntityDestroy(state, level, pos, entity);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
