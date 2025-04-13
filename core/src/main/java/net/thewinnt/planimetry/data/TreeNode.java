package net.thewinnt.planimetry.data;

import net.thewinnt.planimetry.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public record TreeNode<T>(T value, Collection<TreeNode<T>> children) {
    public void visit(Consumer<List<T>> branchVisitor) {
        branchVisitor.accept(List.of(value));
        for (TreeNode<T> i : children) {
            i.visit(branchVisitor, Util.make(new ArrayList<>(), list -> {
                list.add(value);
                list.add(i.value);
            }));
        }
    }

    private void visit(Consumer<List<T>> branchVisitor, ArrayList<T> path) {
        branchVisitor.accept(path);
        for (TreeNode<T> i : children) {
            ArrayList<T> path1 = (ArrayList<T>) path.clone();
            path1.add(i.value);
            i.visit(branchVisitor, path1);
        }
    }
}
