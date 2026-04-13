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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.all.BreakBlock;
import net.sonicrushxii.chaos_emerald.network.all.UpdateHandItem;
import org.jetbrains.annotations.Nullable;

public class SuperEmeraldBlock extends Block implements SimpleWaterloggedBlock {

    public static final VoxelShape SHAPE = Block.box(0,1,0,16,12,16);

    public SuperEmeraldBlock(Properties pProperties) {

        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
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
    public float getDestroyProgress(BlockState pState, net.minecraft.world.entity.player.Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        return 0.0F;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pPlayer.getMainHandItem() == ItemStack.EMPTY && !pLevel.isClientSide) {
            //Playsound
            pLevel.playSound(pPlayer,pPos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5f,1.1f);

            //Remove Block
            pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
            pLevel.updateNeighborsAt(pPos, Blocks.AIR);
            pLevel.updateNeighborsAt(pPos.below(), Blocks.AIR);
            PacketHandler.sendToServer(new BreakBlock(pPos));

            //Update Player Item
            pPlayer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(pState.getBlock().asItem()));
            PacketHandler.sendToServer(new UpdateHandItem(new ItemStack(pState.getBlock().asItem()),InteractionHand.MAIN_HAND));

            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
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
