package org.mri;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.*;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.mri.flow.AxonFlowBuilder;
import org.mri.flow.AxonNode;
import org.mri.output.DotFormat;
import org.mri.output.OutputFormat;
import org.mri.output.PlantUmlFormat;
import org.mri.output.ToStringFormat;
import org.mri.processors.AxonVersion;
import org.mri.processors.ClassHierarchyProcessor;
import org.mri.processors.MethodsExecutionsProcessor;
import org.mri.source.*;
import org.mri.source.eventHandlers.grouping.GroupEventHandlersByEventName;
import org.mri.source.eventHandlers.EventHandlers;
import org.mri.source.eventHandlers.grouping.NoOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.compiler.ModelBuildingException;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

public class ShowAxonFlow {
    enum Format {
        DEFAULT { public OutputFormat printer() { return new ToStringFormat(); } },
        PLANTUML { public OutputFormat printer() { return new PlantUmlFormat(); } },
        DOT { public OutputFormat printer() { return new DotFormat(); } };
        public OutputFormat printer() { throw new UnsupportedOperationException(); }
    }

    private static Logger logger = LoggerFactory.getLogger(ShowAxonFlow.class);

    @Option(name = "-s", aliases = "--source-folder", metaVar = "SOURCE_FOLDERS",
        usage = "source folder(s) for the analyzed project",
        handler = StringArrayOptionHandler.class,
        required = true)
    private List<String> sourceFolders;

    @Option(name = "-m", aliases = "--method-name", metaVar = "METHOD_NAME",
        usage = "method name to print call hierarchy",
        required = true)
    private String methodName;

    @Option(name = "-c", aliases = "--classpath", metaVar = "CLASSPATH",
        usage = "classpath for the analyzed project")
    private String classpath;

    @Option(name = "--classpath-file", metaVar = "CLASSPATH_FILE", usage = "file containing the classpath for the analyzed project",
        forbids = "--classpath")
    private File classpathFile;
    private PrintStream printStream;

    @Option(name = "-f", aliases = "--format", metaVar = "FORMAT",
        usage = "format of the output")
    private Format format = Format.DEFAULT;

    @Option(name = "--match-events-by-name", metaVar = "MATCH_EVENTS_BY_NAME",
        usage = "match events by class name only instead of a full signature")
    private boolean matchEventsByName;

    @Option(name = "--axon-version", metaVar = "AXON_VERSION",
        usage = "axon version used in your code")
    private AxonVersion axonVersion = AxonVersion.V2;

    public static void main(String[] args) throws Exception {
        ShowAxonFlow.parse(args).doMain();
    }

    private static ShowAxonFlow parse(String[] args) {
        ShowAxonFlow showAxonFlow = new ShowAxonFlow(System.out);
        CmdLineParser parser = new CmdLineParser(showAxonFlow, ParserProperties.defaults().withUsageWidth(120));
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.print("Usage: java -jar <CHP_JAR_PATH>" + parser.printExample(OptionHandlerFilter.REQUIRED));
            System.err.println();
            System.err.println();
            System.err.println("Options:");
            parser.printUsage(System.err);
            System.exit(1);
        }
        return showAxonFlow;
    }

    public ShowAxonFlow(PrintStream printStream) {
        this.printStream = printStream;
    }

    public ShowAxonFlow(String classpath, List<String> sourceFolders, String methodName, PrintStream printStream) {
        this(printStream);
        this.sourceFolders = sourceFolders;
        this.methodName = methodName;
        this.classpath = classpath;
    }

    public void doMain() throws Exception {
        Launcher launcher = new Launcher();
        if (classpath != null) {
            launcher.setArgs(new String[]{"--source-classpath", classpath});
        }
        if (classpathFile != null) {
            launcher.setArgs(new String[]{"--source-classpath", StringUtils.strip(FileUtils.readFileToString(classpathFile), "\n\r\t ")});
        }
        for (String sourceFolder : sourceFolders) {
            launcher.addInputResource(sourceFolder);
        }
        try {
            launcher.run();
        } catch (ModelBuildingException e) {
            throw new RuntimeException("You most likely have not specified your classpath. Pass it in using either '--claspath' or '--classpath-file'.", e);
        }

        printAxonFlow(launcher, printStream);
    }

    private void printAxonFlow(Launcher launcher, PrintStream printStream) throws Exception {
        ClassesHierarchy classHierarchy = new ClassesHierarchy();
        MethodsExecutions methodsExecutions = new MethodsExecutions();
        CommandHandlers commandHandlers = new CommandHandlers();
        Aggregates aggregates = new Aggregates();
        EventHandlers eventHandlers = new EventHandlers(
            matchEventsByName
                ? new GroupEventHandlersByEventName()
                : new NoOp()
        );

        launcher.addProcessor(new ClassHierarchyProcessor(classHierarchy));
        launcher.addProcessor(new MethodsExecutionsProcessor(methodsExecutions));
        launcher.addProcessor(axonVersion.eventHandlersProcessor(eventHandlers));
        launcher.addProcessor(axonVersion.commandHandlersProcessor(commandHandlers));
        launcher.addProcessor(axonVersion.aggregatesProcessor(aggregates));
        launcher.process();

        AxonFlowBuilder axonFlowBuilder = new AxonFlowBuilder(
            new MethodCallsHierarchy(methodsExecutions, classHierarchy),
            eventHandlers,
            commandHandlers,
            aggregates
        );

        try {
            List<AxonNode> flow = axonFlowBuilder.buildFlow(methodName);

            OutputFormat printer = format.printer();
            for (AxonNode node : flow) {
                printer.print(node, printStream);
            }
        } catch (MethodNotFoundException e) {
            printStream.println("No method containing `" + e.getMethodName() + "` found.");
        }
    }
}
