import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import javax.naming.CompositeName;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class InspectorSelectVisitor implements SelectVisitor, SelectItemVisitor {
    private InspectorFromItemVisitor fromItemVisitor = new InspectorFromItemVisitor();
    private InspectorSelectItemVisitor selectItemVisitor = new InspectorSelectItemVisitor();
    private boolean foundProblem = false;
    private ArrayList<String> problemList = new ArrayList<String>();
    private ArrayList<Column> accessColumnList = new ArrayList<Column>();
    private Table fromTable;

    @Override
    public void visit(AllColumns allColumns) {
        // TODO Need to inspect if there are columns which are sensitive
        foundProblem = true;
        problemList.add(allColumns.toString());
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        //TODO is this sufficient
        foundProblem = true;
        problemList.add(allTableColumns.toString());
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        foundProblem = true;
        problemList.add(selectExpressionItem.toString());
    }

    @Override
    public void visit(PlainSelect plainSelect) {
    //TODO MAIN EFFORT

        if (plainSelect.getOracleHint() != null) {
            foundProblem = true;
            problemList.add("Oracle Hint");
        }

        if (plainSelect.getSkip() != null) {
            this.foundProblem = true;
            this.problemList.add("SKIP");
        }

        if (plainSelect.getFirst() != null) {
            this.foundProblem = true;
            this.problemList.add("FIRST");
        }

        if (plainSelect.getDistinct() != null) {
            this.foundProblem = true;
            this.problemList.add("DISTINCT");
        }
        if (plainSelect.getTop() != null) {
            this.foundProblem = true;
            this.problemList.add("TOP");
        }
        if (plainSelect.getMySqlSqlNoCache()) {
            //TODO Handle

        }
        if (plainSelect.getMySqlSqlCalcFoundRows()) {
            this.foundProblem = true;
            this.problemList.add("SQL_CALC_FOUND_ROWS");
        }
        //TODO Add selectItems visitor
        for(SelectItem selectItem : plainSelect.getSelectItems()){
            selectItem.accept(selectItemVisitor);
        }
        if(selectItemVisitor.foundProblem()){
            foundProblem = true;
            problemList.addAll(selectItemVisitor.getProblemList());
        } else {
            accessColumnList.addAll(selectItemVisitor.getAccessColumnList());
        }
//        sql.append(getStringList(selectItems));

        if (plainSelect.getIntoTables() != null) {
            this.foundProblem = true;
            this.problemList.add("INTO");
//            sql.append(" INTO ");
//            for (Iterator<Table> iter = intoTables.iterator(); iter.hasNext();) {
//                sql.append(iter.next().toString());
//                if (iter.hasNext()) {
//                    sql.append(", ");
//                }
//            }
        }

        if (plainSelect.getFromItem() != null) {
            //TODO visit FROM item
            plainSelect.getFromItem().accept(fromItemVisitor);
            if(fromItemVisitor.foundProblem()){
                foundProblem = true;
                problemList.addAll(fromItemVisitor.getProblemList());
            } else {
                fromTable = fromItemVisitor.getFromTable();
            }

            if (plainSelect.getJoins() != null) {
                this.foundProblem = true;
                this.problemList.add("JOIN");
//                Iterator<Join> it = joins.iterator();
//                while (it.hasNext()) {
//                    Join join = it.next();
//                    if (join.isSimple()) {
//                        sql.append(", ").append(join);
//                    } else {
//                        sql.append(" ").append(join);
//                    }
//                }
            }

            if (plainSelect.getKsqlWindow() != null) {
                foundProblem = true;
                problemList.add(" WINDOW ");
            }
            if (plainSelect.getWhere() != null) {
                //TODO Where
                foundProblem = true;
                problemList.add(" WHERE ");
            }
            if (plainSelect.getOracleHierarchical() != null) {
                foundProblem = true;
                problemList.add("Oracle Hierarchical");
            }
            if (plainSelect.getGroupBy() != null) {
                //TODO Support ?
                foundProblem = true;
                problemList.add("GROUP BY");
            }
            if (plainSelect.getHaving() != null) {
                //TODO Support ?
                foundProblem = true;
                problemList.add(" HAVING ");
            }
            if(plainSelect.isOracleSiblings()){
                foundProblem = true;
                problemList.add(" Oracle Sibling");
            }
//            sql.append(orderByToString(oracleSiblings, orderByElements));
            if(!(plainSelect.getOrderByElements() == null)){
                foundProblem = true;
                problemList.add(plainSelect.getOrderByElements().toString());
            }
            if (plainSelect.getLimit() != null) {
                foundProblem = true;
                problemList.add("Limit");
            }
            if (plainSelect.getOffset() != null) {
                foundProblem = true;
                problemList.add("Offset");
            }
            if (plainSelect.getFetch() != null) {
                foundProblem = true;
                problemList.add("Fetch");
            }
            if (plainSelect.isForUpdate()) {
                this.foundProblem = true;
                this.problemList.add("FOR UPDATE");

//                if (forUpdateTable != null) {
//                    sql.append(" OF ").append(forUpdateTable);
//                }
//
//                if (wait != null) {
//                    // Wait's toString will do the formatting for us
//                    sql.append(wait);
//                }
            }
            if (plainSelect.getOptimizeFor() != null) {
                this.foundProblem = true;
                this.problemList.add("OPTIMIZE FOR");
            }
        } else {
            //without from
            if (plainSelect.getWhere() != null) {
                //TODO visit WHERE items
                foundProblem = true;
                problemList.add("WHERE");
//                sql.append(" WHERE ").append(where);
            }
        }
        if (plainSelect.getForXmlPath() != null) {
            this.foundProblem = true;
            this.problemList.add("FOR XML PATH");
        }

    }

    @Override
    public void visit(SetOperationList setOpList) {
        //TODO is this sufficient
        foundProblem = true;
        problemList.add(setOpList.toString());
    }

    @Override
    public void visit(WithItem withItem) {
        //TODO is this sufficient
        foundProblem = true;
        problemList.add(withItem.toString());
    }

    @Override
    public void visit(ValuesStatement aThis) {
        //TODO is this sufficient
        foundProblem = true;
        problemList.add(aThis.toString());
    }

    public boolean foundProblem() {
        return foundProblem;
    }

    public ArrayList<String> getProblemList() {
        return problemList;
    }


    public ArrayList<Column> getAccessColumnList() {
        return accessColumnList;
    }
}
