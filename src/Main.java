import java.io.File;

public class Main {

    public static void main(String[] args)
    {
        Experiment experiment = parse();
        System.out.println(experiment);
    }

    /**
     * Parses a file describing an experiment and returns it.
     * @return experiment object
     */
    private static Experiment parse()
    {
        // get access to resources directory
        File resourcesDirectory = new File("src/resources");

        // get a list of all files in resources
        File[] resources = resourcesDirectory.listFiles();

        // create an empty experiment
        Experiment experiment = new Experiment();

        // parse files
        for (File file : resources)
        {
            if (file.isDirectory() && file.getName().contains("Task"))
            {
                if (file.getName().equals("Task1"))
                    experiment.addTask(
                            ExperimentReader.readTask(file, 14, " ", true, "-",
                                    args -> mergeTask1(args))
                    );
                else if (file.getName().equals("Task2"))
                    experiment.addTask(
                            ExperimentReader.readTask(file, 14, " ", false, "/",
                                    args -> mergeTask2(args))
                    );
            }
        }

        return experiment;
    }

    private static String mergeTask1(String[] args)
    {
        return args[0] + "_" + args[1] + "_" + args[2];
    }

    private static String mergeTask2(String[] args)
    {
        return args[0] + "_" + args[1];
    }
}
