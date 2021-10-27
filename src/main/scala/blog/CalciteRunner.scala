package blog

import blog.evalLiteral.{MyRel, Rules}
import org.apache.calcite.plan.RelOptUtil
import org.apache.calcite.rel.{RelNode, RelRoot}
import org.apache.calcite.sql.SqlNode
import org.apache.calcite.tools.{FrameworkConfig, Frameworks}

object CalciteRunner extends App {

  def config: FrameworkConfig = Frameworks.newConfigBuilder()
    .defaultSchema(Frameworks.createRootSchema(false))
    .build()

  val framework = new FrameworkPlanner(config)

  val logical = framework.rel("SELECT 5*6,5+6")

  println(RelOptUtil.toString(logical))

  println(RelOptUtil.toString(framework.optimize(logical)))

}

class FrameworkPlanner(config: FrameworkConfig) {
  private val planner = Frameworks.getPlanner(config)

  def parseAndValidate(sql: String): SqlNode = planner.validate(planner.parse(sql))

  def rel(sqlNode: SqlNode): RelRoot = planner.rel(sqlNode)

  def rel(query: String): RelNode = {
    val sqlNode = parseAndValidate(query)
    val relRoot = rel(sqlNode)
    relRoot.rel
  }

  def optimize(relNode: RelNode): RelNode = {
    val costPlanner = relNode.getCluster.getPlanner
    RelOptUtil.registerDefaultRules(costPlanner, false, false)
    costPlanner.addRule(Rules.VALUES.toRule)
    costPlanner.addRule(Rules.PROJECT.toRule)
    costPlanner.addRule(Rules.EVAL_VALUES.toRule)
    val myRel = costPlanner.changeTraits(relNode,relNode.getTraitSet.replace(MyRel.CONVENTION))
    costPlanner.setRoot(myRel)
    costPlanner.findBestExp()
  }

}