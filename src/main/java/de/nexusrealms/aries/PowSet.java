package de.nexusrealms.aries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDamageTypes;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerReference;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;

import java.util.List;

public record PowSet(List<PowSetLevel> levels) {
    public static final RegistryKey<Registry<PowSet>> KEY = RegistryKey.ofRegistry(Aries.id("pow_set"));
    public static final Codec<RegistryEntry<PowSet>> ENTRY_CODEC = RegistryFixedCodec.of(KEY);
    public static final Codec<PowSet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PowSetLevel.CODEC.listOf().fieldOf("levels").forGetter(PowSet::levels)
    ).apply(instance, PowSet::new));
    public record PowSetLevel(String translationKey, List<PowerReference> powers){
        public static final Codec<PowSetLevel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("translationKey").forGetter(PowSetLevel::translationKey),
                ApoliDataTypes.POWER_REFERENCE.codec().listOf().fieldOf("powers").forGetter(PowSetLevel::powers)
        ).apply(instance, PowSetLevel::new));
    }
    public static void init(){
        DynamicRegistries.registerSynced(KEY, CODEC);
    }
}
