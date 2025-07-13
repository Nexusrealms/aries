package de.nexusrealms.aries;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PowSetComponent implements AutoSyncedComponent {
    private List<PowSetInstance> list = new ArrayList<>();
    private final PlayerEntity player;
    public static final ComponentKey<PowSetComponent> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(Aries.id("pow_sets"), PowSetComponent.class);
    public PowSetComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        list = Aries.mutableListOf(PowSetInstance.CODEC).parse(wrapperLookup.getOps(NbtOps.INSTANCE), nbtCompound.get("list")).getOrThrow();
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.put("list", PowSetInstance.CODEC.listOf().encodeStart(wrapperLookup.getOps(NbtOps.INSTANCE), list).getOrThrow());
    }
    public Optional<PowSetInstance> apply(PowSetInstance instance){
        if (list.stream().anyMatch(instance1 -> instance1.powSet().matchesKey(instance.powSet().getKey().get()) && instance1.level() == instance.level())) {
            return Optional.empty();
        }
        boolean bool = list.removeIf(instance1 -> instance1.powSet().matchesKey(instance.powSet().getKey().get()));
        list.add(instance);
        PowerHolderComponent powers = PowerHolderComponent.KEY.get(player);
        powers.removeAllPowersFromSource(instance.getId());
        instance.getPowers().forEach(powerReference -> powers.addPower(powerReference, instance.getId()));
        powers.sync();
        KEY.sync(player);
        PowSetEvents.APPLY.invoker().call(instance, (ServerPlayerEntity) player, false, !bool);
        return Optional.of(instance);
    }
    public Optional<PowSetInstance> remove(RegistryKey<PowSet> key){
        PowerHolderComponent powers = PowerHolderComponent.KEY.get(player);
        powers.removeAllPowersFromSource(key.getValue());
        powers.sync();
        Optional<PowSetInstance> instance = list.stream().filter(instance1 -> instance1.powSet().matchesKey(key)).findAny();
        list.removeIf(instance1 -> instance1.powSet().matchesKey(key));
        KEY.sync(player);
        instance.ifPresent(powSetInstance -> PowSetEvents.REMOVE.invoker().call(powSetInstance, (ServerPlayerEntity) player));
        return instance;
    }
    public Optional<PowSetInstance> upgrade(RegistryKey<PowSet> key){
        Optional<PowSetInstance> old = list.stream().filter(instance -> instance.powSet().matchesKey(key)).findAny();
        if(old.isEmpty()) Optional.empty();
        Optional<PowSetInstance> instance = old.get().withLevel(old.get().level() + 1);
        if(instance.isEmpty()){
            remove(key);
            apply(instance.get());
            KEY.sync(player);
            PowSetEvents.APPLY.invoker().call(instance.get(), (ServerPlayerEntity) player, true, false);
        }
        return instance;
    }
    public int getLevel(RegistryKey<PowSet> key){
       return list.stream().filter(instance1 -> instance1.powSet().matchesKey(key)).findAny().map(PowSetInstance::level).orElse(-1);
    }
}
