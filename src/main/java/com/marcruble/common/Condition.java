package com.marcruble.common;

/**
 * Represents a condition in the experiment identified by a string
 * of characters.
 */
public class Condition {

    // identifier of this condition
    private String id;

    public Condition(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    /**
     * Returns the condition in a more readable version for a GUI.
     * @return readable string
     */
    public String toReadableString()
    {
        /** IMPLEMENT ME **/
        return toString();
    }

    @Override
    public String toString()
    {
        return  id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof Condition))
            return false;

        return id.equals(((Condition)obj).getId());
    }
}
