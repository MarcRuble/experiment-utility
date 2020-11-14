import com.tree.TreeNode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

public class ExperimentReader {

    /**
     * Parses the description of a task with given arguments.
     * @param rootFile folder containing the description
     * @param numLines number of rows to be read in each file
     * @param separator between the identifiers, e.g. " " in line: 1 2 3
     * @param collapse collapse the last level of identifiers
     * @param connector how to connect the collapsed identifiers, e.g. with "-": 1 2 3 -> 1-2-3
     * @param merger function mapping the read identifiers in all levels to a key/name for the condition
     * @return task object describing what was read
     */
    public static Task readTask(File rootFile, int numLines, String separator,
                                boolean collapse, String connector, Function<String[], String> merger)
    {
        if (!rootFile.isDirectory())
            throw new IllegalArgumentException("Provided directory was not a directory: " + rootFile.getPath());

        // use a tree to contain the different maps while traversing the file structure
        TreeNode<Map<Integer, List<String>>> tree =
                readNode("NULL", rootFile, numLines, separator, collapse, connector);

        // create empty task
        Task task = new Task();

        // build the table of combined conditions
        // for each subject id
        for (int subject = 0; subject < numLines; subject++)
        {
            // add a fresh row
            task.addRow();

            // determine conditions for this subject in tree
            List<Condition> conditions = mergeTree(subject, tree, new ArrayList<>(), merger);

            // add them to the task
            for (Condition cond : conditions)
                task.addCondition(cond);
        }

        // finished building task for all subjects
        return task;
    }

    /**
     * Creates a tree node containing all read information in this directory including recursive subdirectories.
     * @param id for this directory/identifier
     * @param directory to read
     * @param numLines number of rows to be read in each file
     * @param separator between the identifiers, e.g. " " in line: 1 2 3
     * @param collapse collapse the last level of identifiers
     * @param connector how to connect the collapsed identifiers, e.g. with "-": 1 2 3 -> 1-2-3
     * @return tree node mapping subject id to a list of identifiers (-1 for encoded parameter id)
     */
    private static TreeNode<Map<Integer, List<String>>> readNode
            (String id, File directory, int numLines, String separator, boolean collapse,
             String connector)
    {
        if (!directory.isDirectory())
            throw new IllegalArgumentException("Provided directory was not a directory: " + directory.getPath());

        // setup tree node for storing the results
        TreeNode<Map<Integer, List<String>>> tree = new TreeNode<>(new HashMap<>());

        // store the given identifier for index -1 to encode an annotated edge
        List<String> onlyID = new ArrayList<>();
        onlyID.add(id);
        tree.data.put(-1, onlyID);

        // get single file at this level
        File singleFile = findFile(directory.listFiles(), ".txt", false);

        // read single file at this level
        List<String> contentLines = readAllLines(directory, singleFile.getName());

        // for each row
        for (int i = 0; i < numLines; i++)
        {
            // parse and save this line
            List<String> line = parseLine(contentLines.get(i), separator);
            tree.data.put(i, line);
        }

        // determine if there are more levels
        File[] subDirs = directory.listFiles(File::isDirectory);

        // use helper function to get set of all identifiers at this level
        Set<String> childrenIDs = collectIdentifiers(tree);
        int createdChildren = 0;

        // for each identifier
        for (String childID : childrenIDs)
        {
            // determine folder for this identifier
            File childFolder = findFile(subDirs, childID, false);

            if (childFolder == null)
            {
                // no folder found -> reached leaf/bottom of file structure
                if (createdChildren > 0)
                    throw  new RuntimeException("Invalid File Structure: Some of the used identifiers " +
                            "were provided a subfolder while " + childID + " did not in " + directory.getPath());

                // collapse this level if required
                if (collapse)
                    tree = collapseNode(tree, connector);

                // finished
                return tree;
            }

            // folder is found -> create child recursively
            createdChildren++;

            tree.addChildNode(readNode(childID, childFolder, numLines, separator, collapse, connector));
        }

        // finished adding children
        return tree;
    }

    /**
     * Performs depth-first traversal on the given tree and merges the identifiers from root to each leaf which
     * is found based on the specified order in the node contents.
     * @param subject for which to traverse the tree
     * @param tree to traverse
     * @param previousIDs list of identifiers seen so far (from root to current without branches)
     * @param merger function mapping the read identifiers in all levels to a key/name for the condition
     * @return list of conditions for this subject
     */
    private static List<Condition> mergeTree(int subject, TreeNode<Map<Integer, List<String>>> tree,
                                             List<String> previousIDs, Function<String[], String> merger)
    {
        // copy to prevent errors
        List<String> currentIDs = new ArrayList<>(previousIDs);

        // add this node's identifier, encoded in -1
        String treeID = tree.data.get(-1).get(0);

        if (!treeID.equals("NULL")) // skip root name
            currentIDs.add(treeID);

        // create list for result
        List<Condition> conditions = new ArrayList<>();

        if (tree.isLeaf())
        {
            // reached leaf node -> go through leaf content
            for (String leafID : tree.data.get(subject))
            {
                // copy current list and extend
                List<String> currentCopy = new ArrayList<>(currentIDs);
                currentCopy.add(leafID);

                // merge from root until here
                Condition condition = new Condition(merger.apply(currentCopy.toArray(new String[0])));
                conditions.add(condition);
            }
        }
        else
        {
            // go recursive for children and combine their results
            // for all children
            for (String childID : tree.data.get(subject))
            {
                // get child node for this child identifier
                TreeNode<Map<Integer, List<String>>> childNode = findChild(tree, childID);

                // go recursive to get all underlying conditions
                List<Condition> childConditions = mergeTree(subject, childNode, currentIDs, merger);

                // add them to the list
                conditions.addAll(childConditions);
            }
        }

        return conditions;
    }

    /**
     * Returns a set of all identifiers in the given node (ignoring children).
     * @param node to search in
     * @return set of identifiers
     */
    private static Set<String> collectIdentifiers(TreeNode<Map<Integer, List<String>>> node)
    {
        Set<String> ids = new HashSet<>();

        for (int key : node.data.keySet())
        {
            if (key >= 0) // ignore -1
                ids.addAll(node.data.get(key));
        }

        return ids;
    }

    /**
     * Returns the child with given identifier or null if not found.
     * @param node to search in
     * @param id to find
     * @return child node or null
     */
    private static TreeNode<Map<Integer, List<String>>> findChild
            (TreeNode<Map<Integer, List<String>>> node, String id)
    {
        if (node.isLeaf())
            return null;

        for (TreeNode<Map<Integer, List<String>>> child : node.children)
        {
            if (child.data.get(-1).get(0).equals(id))
                return child;
        }

        return null;
    }

    /**
     * Collapses a given list of strings to list of a single string.
     * @param list of strings
     * @param connector between the strings
     * @return one element list
     */
    private static List<String> collapseList(List<String> list, String connector)
    {
        List<String> c = new ArrayList<>();
        c.add(String.join(connector, list));
        return c;
    }

    /**
     * Collapses a given tree node's content list (preserving the -1 entry).
     * @param node to collapse
     * @param connector between the identifiers
     * @return new tree node
     */
    private static TreeNode<Map<Integer, List<String>>> collapseNode
            (TreeNode<Map<Integer, List<String>>> node, String connector)
    {
        TreeNode<Map<Integer, List<String>>> collapsed = new TreeNode<>(new HashMap<>());

        for (int key : node.data.keySet())
        {
            if (key >= 0)
                collapsed.data.put(key, collapseList(node.data.get(key), connector));
            else
                collapsed.data.put(key, node.data.get(key));
        }

        return collapsed;
    }

    /**
     * Parses a single line of identifiers and returns them in a list.
     * @param line of content
     * @param separator between identifiers
     * @return list of identifiers
     */
    private static List<String> parseLine(String line, String separator)
    {
        List<String> conditions = new ArrayList<>();
        String[] parts = line.strip().split(separator);

        // for each part of the line
        for (String part : parts)
        {
            part = part.strip();

            if (part.length() > 0)
                conditions.add(part);
        }

        return conditions;
    }

    /**
     * Returns a file with a given starting name.
     * @param files to search through
     * @param name of file (either how it starts or ends
     * @param starting if the name is start or end of filename
     * @return file object
     */
    private static File findFile(File[] files, String name, boolean starting)
    {
        for (File file : files)
        {
            if (starting && file.getName().startsWith(name)
                || !starting && file.getName().endsWith(name))
                return file;
        }
        return null;
    }

    /**
     * Reads and returns all lines separately from a given file.
     * Note: Ignores lines starting with # as comments.
     * @param directory of file
     * @param fileName of file
     * @return list of line strings
     */
    private static List<String> readAllLines(File directory, String fileName)
    {
        if (!directory.isDirectory())
            throw new IllegalArgumentException("Provided directory was not a directory: " + directory.getPath());

        List<String> lines = null;

        try {
            String filePath = directory.getPath() + "/" + fileName;
            Path path = Paths.get(filePath);
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        lines.removeIf(l -> l.stripLeading().startsWith("#") || l.strip().isEmpty());
        return lines;
    }
}
