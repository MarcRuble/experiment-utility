# Experiment Utility
Contains utility classes for generating description files for experiments and reading such descriptions to a simple model.

*Note:* The reader uses [yet another tree structure](https://github.com/gt4dev/yet-another-tree-structure) by gt4dev, slightly modified by adding another method for adding a child.

## Generators
The package `generators` currently contains two kinds of file generators which are executed without arguments. The parameters for file generation are specified in a command-line dialogue.\
Please see folder `examples/exampleResults` for an example of generated output files.

### Balanced Latin Square Generator
This generator takes two arguments:
* `number of possible conditions`: The resulting table will contain condition identifiers from `1` up to this number.
* `name of file`: Where the resulting table will be saved. If not already in name, it will be extended with `.txt` automatically.

The result will be a balanced latin square, as defined here:
> Martyn Shuttleworth (May 8, 2009). Counterbalanced Measures Design. Retrieved Dec 05, 2020 from Explorable.com: https://explorable.com/counterbalanced-measures-design

For odd numbers of conditions, the mirrored extension is automatically applied.

### Random Square Generator
This generator takes following arguments:
* `seed`: Arbitrary integer number for the pseudo-random number generator used for all files created in this execution.
* `number of rows`: In all of the created files.
* `number of columns`: In all of the created files.
* `number of possible conditions`: The resulting table will contain condition identifiers from `1` up to this number.
* `number of files`: All are randomly generated using the same pseudo-random number generator.
* `name of file`: Starting name of all files which is extended by the number and extension `.txt` automatically.

## Reader
The package `reader` offers an `ExperimentReader` class to parse a folder of description files into a model of simple Java objects which can be found in the package `common`. 

### Model
The classes include:
* `Experiment`: Ordered list of `Task` objects.
* `Task`: Table mapping each subject ID to an ordered list of `Condition` objects.
* `Condition`: Represents a single experimental condition/trial/subtask which can be identified with a `string`.

### Should I use the reader?
Check if your experimental procedure can be described with the hierarchy described just above in the Model. In genneral, user studies with a range of experimental conditions, possibly multiple independent variables with hierarchical order, can be described in this fashion.\
For example lets assume, we want to conduct an experiment to compare 3 kinds of VR controllers (`controller = {1, 2, 3}`) in 2 positions (`pose = {sit, stand}`). So our participants perform tasks with each of the controllers and within these 3 blocks, also the position is varied:
* `0` does `1-sit` then `1-stand` then `2-stand` and so on...
* `1` does `2-sit` then `2-stand` then `3-stand` and so on...
* ...

This can easily be described with the above model as an `Experiment` with only one `Task` where each partipant has a different line of `Conditions`.

### How to describe my experiment?
Sticking to our example from above, we now want to write the description files for our VR experiment. Therefore please have a look into the folder `examples/exampleDescription` which describes exactly this case. As we can see, an experiment is described as a folder of files:
1. There is always a `.txt` file describing the order of  conditions (identifiers in row) for each participant (each row).
2. Then there are a number of subdirectories which names end with the identifier in the `.txt` file. E.g. folder `Controller1` contains the part of the experiment for `controller = 1`.

For more complex experiments, this structure can be nested arbitrarily. See `src/resources/` for a larger example of description files.

### How to use the reader?
When a description of the experiment in this format is at hand, one can use the `ExperimentReader.readTask()` method to parse the experiment in the above described model classes. See `Main.java` for an example of this.\
The parameter `collapse` indicates if the specified conditions in the lowest level of file directories should be collapsed to a single condition. Then a line in the `.txt` file with `1 2 3` would be interpreted as one condition `1-2-3` instead of 3 subsequent ones (where `-` can be replaced by other symbols with the `connector` parameter).\
Still there is one more thing that we need to specify in order to parse our experiment, the `merger` function. Because we have defined the orders of conditions of our 2 independent variables in separate files, we need to merge them into one. E.g. we could define a function which takes as input an array of `string` and simply joins them into a single `string` with `_` as separator. This way we obtain conditions of form `1_sit` or `3_stand` which are easy to work with.
