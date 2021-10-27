package blog.evalLiteral

import com.google.common.collect.ImmutableList
import org.apache.calcite.interpreter.JaninoRexCompiler
import org.apache.calcite.plan.{RelOptRule, RelOptRuleCall, RelRule}
import org.apache.calcite.rel.`type`.RelDataType
import org.apache.calcite.rel.core.{Project, Values}
import org.apache.calcite.rex.{RexBuilder, RexInputRef, RexLiteral, RexNode}

import scala.jdk.CollectionConverters.{ListHasAsScala, SeqHasAsJava}

object EvalValuesRule {

  trait Config extends RelRule.Config {
    override def toRule: RelOptRule = new EvalValuesRule(this)
  }

}

class EvalValuesRule(config: EvalValuesRule.Config) extends RelRule[EvalValuesRule.Config](config) {
  override def onMatch(call: RelOptRuleCall): Unit = {
    val project = call.rel(0).asInstanceOf[Project]
    val values = call.rel(1).asInstanceOf[Values]

    val rexBuilder = project.getCluster.getRexBuilder

    val newLiterals = evaluate(project.getProjects, project.getRowType, values, rexBuilder)
    call.transformTo(
      new MyLogicalValues(
        values.getCluster,
        project.getRowType,
        newLiterals,
        project.getTraitSet
      )
    )
  }

  def evaluate(
                exprs: java.util.List[RexNode],
                inputRowType: RelDataType,
                values: Values,
                rexBuilder: RexBuilder
              ): ImmutableList[ImmutableList[RexLiteral]] = {
    val compiler = new JaninoRexCompiler(rexBuilder)
    val execution = exprs.asScala
      .map({
        case ref: RexInputRef =>
          values.getTuples.get(0).get(ref.getIndex).getValue2
        case expr =>
          val scalar = compiler.compile(ImmutableList.of(expr), inputRowType)
          scalar.execute(null)
      })
    val literalValues = execution.zipWithIndex.map {
      case (ref, index) =>
        rexBuilder.makeLiteral(ref, inputRowType.getFieldList.get(index).getType, true)
          .asInstanceOf[RexLiteral]
    }
      .asJava
    ImmutableList.of(ImmutableList.copyOf(literalValues))
  }

}
