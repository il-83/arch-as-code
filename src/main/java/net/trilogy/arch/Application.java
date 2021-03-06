package net.trilogy.arch;

import lombok.Builder;
import lombok.Generated;
import net.trilogy.arch.adapter.architectureUpdate.ArchitectureUpdateReader;
import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.adapter.google.GoogleDocsAuthorizedApiFactory;
import net.trilogy.arch.adapter.structurizr.StructurizrAdapter;
import net.trilogy.arch.commands.DiffCommand;
import net.trilogy.arch.commands.ImportCommand;
import net.trilogy.arch.commands.InitializeCommand;
import net.trilogy.arch.commands.ListComponentsCommand;
import net.trilogy.arch.commands.ParentCommand;
import net.trilogy.arch.commands.PublishCommand;
import net.trilogy.arch.commands.ValidateCommand;
import net.trilogy.arch.commands.architectureUpdate.AuAnnotateCommand;
import net.trilogy.arch.commands.architectureUpdate.AuCommand;
import net.trilogy.arch.commands.architectureUpdate.AuFinalizeAndPublishCommand;
import net.trilogy.arch.commands.architectureUpdate.AuInitializeCommand;
import net.trilogy.arch.commands.architectureUpdate.AuNewCommand;
import net.trilogy.arch.commands.architectureUpdate.AuPublishStoriesCommand;
import net.trilogy.arch.commands.architectureUpdate.AuValidateCommand;
import net.trilogy.arch.config.AppConfig;
import net.trilogy.arch.facade.FilesFacade;
import picocli.CommandLine;

import static java.lang.System.exit;

@Builder
@Generated // Lie to JaCoCo
public class Application {
    @Builder.Default
    private final AppConfig appConfig = AppConfig.builder().build();

    @Builder.Default
    private final StructurizrAdapter structurizrAdapter = new StructurizrAdapter();
    @Builder.Default
    private final GoogleDocsAuthorizedApiFactory googleDocsAuthorizedApiFactory = new GoogleDocsAuthorizedApiFactory();
    @Builder.Default
    private final FilesFacade filesFacade = new FilesFacade();
    @Builder.Default
    private final GitInterface gitInterface = new GitInterface();

    public static void main(String[] args) {
        final var app = Application.builder().build();

        int exitCode = app.execute(args);

        if (exitCode != 0) {
            app.getCli()
                    .getCommandSpec()
                    .commandLine()
                    .getOut()
                    .println("Command failed, for more info please check log file at: " + System.getProperty("user.home") +
                            "/.arch-as-code/arch-as-code.log");
        }

        exit(exitCode);
    }

    private CommandLine getCli() {
        return new CommandLine(new ParentCommand())
                .addSubcommand(new InitializeCommand(filesFacade))
                .addSubcommand(new ValidateCommand())
                .addSubcommand(new PublishCommand(structurizrAdapter))
                .addSubcommand(new ImportCommand(filesFacade))
                .addSubcommand(new ListComponentsCommand(filesFacade))
                .addSubcommand(new DiffCommand(filesFacade, gitInterface, new ArchitectureUpdateReader(filesFacade)))
                .addSubcommand(new CommandLine(new AuCommand())
                        .addSubcommand(new AuInitializeCommand(filesFacade))
                        .addSubcommand(new AuNewCommand(googleDocsAuthorizedApiFactory, filesFacade, gitInterface))
                        .addSubcommand(new AuValidateCommand(filesFacade, gitInterface))
                        .addSubcommand(new AuPublishStoriesCommand(filesFacade, gitInterface))
                        .addSubcommand(new AuAnnotateCommand(filesFacade))
                        .addSubcommand(new AuFinalizeAndPublishCommand(filesFacade, gitInterface)));
    }

    public int execute(String[] args) {
        return getCli().execute(args);
    }
}
