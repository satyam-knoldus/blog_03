package blog.evalLiteral

import org.apache.calcite.plan.RelOptRule
import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.convert.ConverterRule
import org.apache.calcite.rel.core.Project

object LogicalProjectConverter extends ConverterRule(Rules.PROJECT) {
  override def convert(rel: RelNode): RelNode = {
    val project = rel.asInstanceOf[Project]
    new MyLogicalProject(
      project.getCluster,
      project.getTraitSet.replace(getOutTrait),
      project.getHints,
      RelOptRule.convert(project.getInput, getOutTrait),
      project.getProjects,
      project.getRowType
    )
  }
}
