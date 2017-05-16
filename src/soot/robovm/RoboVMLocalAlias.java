package soot.robovm;

import soot.Local;
import soot.Type;
import soot.UnitPrinter;
import soot.jimple.internal.JimpleLocal;
import soot.util.Switch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Demyan Kimitsa
 * This is special wrapper around of JimpleLocal required re-create all local variables from local variable table.
 * It works as following:
 * JVM operates with variable slots and different local variables would share same slot at different lines of code
 * (e.g. they shall never cross)
 *
 * The idea for all locals that share same slot create one slot variable (owner) and for each local variable that refers
 * to this slot -- separate alias (with different variableTableIndex)
 * Idea2 is to make all aliases to look exactly same as their owner variable. so equal(), name() and hashCode() is redirected
 * to owner. This makes all logic think that it is working with exactly same slot local variable.
 * Idea3 is during LocalSplit is to split into real JimpleLocal but keeping variableTableIndex. So after split there will
 * be new variable with new name but with proper variableTableIndex. All extra copies will be removed during localVariableMerge
 * and unused variable removal
 */
public class RoboVMLocalAlias extends JimpleLocal {
    private final Local owner;
    private Map<Integer, RoboVMLocalAlias> aliases;

    public RoboVMLocalAlias(Local owner, int variableTableIndex) {
        super(owner.getName(), owner.getType());
        this.owner = owner;
        super.setVariableTableIndex(variableTableIndex);
    }

    /**
     *
     */
    public RoboVMLocalAlias getAlias(int variableTableIndex) {
        if (variableTableIndex == getVariableTableIndex())
            return this;
        RoboVMLocalAlias alias = null;
        if (aliases != null)
            alias = aliases.get(variableTableIndex);
        else
            aliases = new HashMap<>();
        if (alias == null) {
            alias = new RoboVMLocalAlias(this.owner, variableTableIndex);
            aliases.put(variableTableIndex, alias);
        }

        return alias;
    }

    @Override
    public boolean equivTo(Object o) {
        return owner.equals(o);
    }

    @Override
    public int equivHashCode() {
        return owner.equivHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RoboVMLocalAlias)
            obj = ((RoboVMLocalAlias)obj).owner;
        return owner.equals(obj);
    }

    @Override
    public int hashCode() {
        return owner.hashCode();
    }

    @Override
    public String getName() {
        return owner.getName();
    }

    @Override
    public void setName(String name) {
        owner.setName(name);
    }

    @Override
    public List getUseBoxes() {
        return owner.getUseBoxes();
    }

    @Override
    public Type getType() {
        return owner.getType();
    }

    @Override
    public void setType(Type t) {
        owner.setType(t);
    }

    @Override
    public void apply(Switch sw) {
        owner.apply(sw);
    }

    @Override
    public Object clone() {
        Local cloned = (Local) owner.clone();
        cloned.setVariableTableIndex(getVariableTableIndex());
        return cloned;
    }

    @Override
    public void toString(UnitPrinter up) {
        up.local(this);
    }

    @Override
    public void setVariableTableIndex(int variableTableIndex) {
        throw new UnsupportedOperationException();
    }
}
