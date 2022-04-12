package edu.byu.cs329.constantfolding;

import edu.byu.cs329.utils.ExceptionUtils;
import edu.byu.cs329.utils.TreeModificationUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.NumberLiteral;

public class EqualityInfixExpressionFolding implements Folding {
    class Visitor extends ASTVisitor {
        public boolean didFold = false;

        @Override
        public void endVisit(InfixExpression node) {
            Operator operator = node.getOperator();
            // if not '==' ignore
            if (operator != InfixExpression.Operator.EQUALS) {
                return;
            }
            // if not number literal, then ignore
            Expression leftOperand = node.getLeftOperand();
            if (!(leftOperand instanceof NumberLiteral)) {
                return;
            }
            // if not number literal, then ignore
            Expression rightOperand = node.getRightOperand();
            if (!(rightOperand instanceof NumberLiteral)) {
                return;
            }
            // current node belongs in top - foldable
            // getToken = string represation of a number literal, then turn it into int
            int left = Integer.decode(((NumberLiteral) leftOperand).getToken());
            int right = Integer.decode(((NumberLiteral) rightOperand).getToken());
            // evaluate
            boolean literal = (left == right);
            // get ast, create new node, replace curr node with new node
            AST ast = node.getAST();
            ASTNode newNode = ast.newBooleanLiteral(literal);
            TreeModificationUtils.replaceChildInParent(node, newNode);
            didFold = true;
        }
    }

    /**
     * Replaces Equals literals in the tree with the literals.
     *
     * <p>
     * Visits the root and any reachable nodes from the root to replace any
     * EqualityExpression reachable node containing a literal with the literal
     * itself.
     *
     * <p>
     * top := all nodes reachable from root such that each node is an outermost
     * parenthesized expression that ends in a literal
     *
     * <p>
     * parents := all nodes such that each one is the parent of some node in top
     *
     * <p>
     * isFoldable(n) := isParenthesizedExpression(n) /\ ( isLiteral(expression(n))
     * || isFoldable(expression(n)))
     *
     * <p>
     * literal(n) := if isLiteral(n) then n else literal(expression(n))
     *
     * @modifies nodes in parents
     *
     * @requires root != null
     * @requires (root instanceof CompilationUnit) \/ parent(root) != null
     *
     * @ensures fold(root) == (old(top) != emptyset)
     * @ensures forall n in old(top), exists n' in nodes
     *
     * @param root the root of the tree to traverse.
     * @return true if equality literals were replaced in the rooted tree
     */
    @Override
    public boolean fold(ASTNode root) {
        checkRequires(root);
        Visitor visitor = new Visitor();
        root.accept(visitor);
        return false;
    }

    private void checkRequires(final ASTNode root) {
        ExceptionUtils.requiresNonNull(root, "Null root passed to EqualityInfixExpressionFolding.fold");
        if (!(root instanceof CompilationUnit) && root.getParent() == null) {
            ExceptionUtils.throwRuntimeException(
                    "Non-CompilationUnit root with no parent passed"
                            +
                            "to EqualityInfixExpressionFolding.fold");
        }
    }
}
