package blog.evalLiteral

import com.google.common.collect.ImmutableList
import org.apache.calcite.plan.{Convention, RelOptCluster, RelTraitSet}
import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.`type`.RelDataType
import org.apache.calcite.rel.core.{Project, Values}
import org.apache.calcite.rel.hint.RelHint
import org.apache.calcite.rex.{RexLiteral, RexNode}

import java.util

sealed trait MyRel extends RelNode

object MyRel {
  val CONVENTION = new Convention.Impl("MyRelTrait", classOf[MyRel])
}

class MyLogicalValues(cluster: RelOptCluster,
                      rowType: RelDataType,
                      tuples: ImmutableList[ImmutableList[RexLiteral]],
                      traitSet: RelTraitSet)
  extends Values(cluster, rowType, tuples, traitSet) with MyRel {
  override def copy(traitSet: RelTraitSet, inputs: util.List[RelNode]): RelNode = new MyLogicalValues(
    getCluster, getRowType, getTuples, traitSet
  )
}

class MyLogicalProject(cluster: RelOptCluster,
                       traitSet: RelTraitSet,
                       hints: java.util.List[RelHint],
                       input: RelNode,
                       projects: java.util.List[RexNode],
                       rowType: RelDataType)
  extends Project(cluster, traitSet, hints, input, projects, rowType) with MyRel {
  override def copy(traitSet: RelTraitSet, input: RelNode, projects: util.List[RexNode], rowType: RelDataType): Project = {
    new MyLogicalProject(
      getCluster,
      traitSet,
      getHints,
      input,
      projects,
      rowType
    )
  }
}