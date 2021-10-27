package blog.evalLiteral

import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.convert.ConverterRule
import org.apache.calcite.rel.core.Values

object LogicalValuesConverter extends ConverterRule(Rules.VALUES) {
  override def convert(rel: RelNode): RelNode = {
    val values = rel.asInstanceOf[Values]
    new MyLogicalValues(
      values.getCluster,
      values.getRowType,
      values.getTuples,
      values.getTraitSet.replace(getOutTrait)
    )
  }
}
