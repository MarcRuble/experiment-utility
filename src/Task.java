import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a task in an experiment containing a table
 * of rows of conditions, one for each participant/group.
 */
public class Task {

    // mapping participant/group to their conditions
    private Map<Integer, List<Condition>> conditionTable;

    // highest assigned index in table
    private int highest = -1;

    public Task()
    {
        conditionTable = new HashMap<>();
    }

    public void addRow()
    {
        highest++;
        conditionTable.put(highest, new ArrayList<Condition>());
    }

    public void addCondition(Condition condition)
    {
        conditionTable.get(highest).add(condition);
    }

    public Condition getCondition(int participant, int index)
    {
        return conditionTable.get(participant).get(index);
    }

    public List<Condition> getConditions(int participant)
    {
        return conditionTable.get(participant);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= highest; i++)
        {
            sb.append(toString(i));
            sb.append("\n-----\n");
        }

        return sb.toString();
    }

    /**
     * Alternative toString which contains only parts relevant for the given participant.
     * @param participant
     * @return
     */
    public String toString(int participant)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(participant);
        sb.append(":   ");

        for (int j = 0; j < getConditions(participant).size(); j++)
        {
            sb.append(getCondition(participant, j).toReadableString());

            if (j < getConditions(participant).size()-1)
                sb.append(" | ");
        }

        return sb.toString();
    }
}
