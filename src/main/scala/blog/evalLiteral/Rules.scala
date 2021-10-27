package blog.evalLiteral

import org.apache.calcite.plan.{Convention, RelRule}
import org.apache.calcite.rel.convert.ConverterRule
import org.apache.calcite.rel.logical.{LogicalProject, LogicalValues}

object Rules {

  val PROJECT: ConverterRule.Config = ConverterRule.Config.INSTANCE
    .withConversion(classOf[LogicalProject], Convention.NONE, MyRel.CONVENTION, "LogicalProjectToMyProject")
    .withRuleFactory(_ => LogicalProjectConverter)

  val VALUES: ConverterRule.Config = ConverterRule.Config.INSTANCE
    .withConversion(classOf[LogicalValues], Convention.NONE, MyRel.CONVENTION, "LogicalValuesToMyLogicalValues")
    .withRuleFactory(_ => LogicalValuesConverter)

  val EVAL_VALUES: EvalValuesRule.Config = RelRule.Config.EMPTY
    .withDescription("Evaluate Literals")
    .withOperandSupplier(b0 =>
      b0.operand(classOf[MyLogicalProject])
        .oneInput(b1 =>
          b1.operand(classOf[MyLogicalValues])
            .noInputs()
        )
    ).as(classOf[EvalValuesRule.Config])


}
