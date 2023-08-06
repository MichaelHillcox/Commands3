package pro.mikey.ccc.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class CccDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(Lang::new);
    }

    private static class Lang extends FabricLanguageProvider {
        protected Lang(FabricDataOutput dataOutput) {
            super(dataOutput, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder builder) {
            builder.add("ccc.generic.home", "Home");
            builder.add("ccc.generic.homes", "Homes (%s)");
            builder.add("ccc.commands.feedback.home.no_homes", "No homes set!");
            builder.add("ccc.commands.feedback.home.added", "Home [%s] added!");
            builder.add("ccc.commands.feedback.home.update", "Home [%s] updated!");
            builder.add("ccc.commands.feedback.home.remove", "Home [%s] removed!");
            builder.add("ccc.commands.feedback.home.clear_all", "All homes removed!");
            builder.add("ccc.commands.feedback.home.teleport", "Teleported to home [%s]!");
            builder.add("ccc.commands.response.home.level_missing", "The dimension [%s] of this home seems to no longer exist...");
            builder.add("ccc.commands.response.home.missing", "The home [%s] does not exist...");
        }
    }
}
