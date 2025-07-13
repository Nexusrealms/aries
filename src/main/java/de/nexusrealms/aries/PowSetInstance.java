package de.nexusrealms.aries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.power.PowerReference;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public record PowSetInstance(RegistryEntry<PowSet> powSet, int level) {
    public static final Codec<PowSetInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PowSet.ENTRY_CODEC.fieldOf("pow_set").forGetter(PowSetInstance::powSet),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("level").forGetter(PowSetInstance::level)
    ).apply(instance, PowSetInstance::new));
    public Identifier getId(){
        return powSet.getKey().orElseThrow(() -> new IllegalStateException("Unregister PowSet!")).getValue();
    }
    public List<PowerReference> getPowers(){
        if (powSet.value().levels().size() < level) {
            throw new IndexOutOfBoundsException("Invalid level for PowSetInstance");
        }
        return powSet().value().levels().get(level).powers();
    }
    public Optional<PowSetInstance> withLevel(int level){
        if(powSet.value().levels().size() < level){
            return Optional.empty();
        }
        return Optional.of(new PowSetInstance(powSet, level));
    }
}
