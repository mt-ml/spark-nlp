package com.johnsnowlabs.nlp

import org.apache.spark.ml.Model
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.sql.Column
import org.apache.spark.sql.types._

/**
  * Created by jose on 25/01/18.
  */
trait RawAnnotator[M<:Model[M]] extends Model[M]
    with ParamsAndFeaturesWritable
    with HasAnnotatorType
    with HasInputAnnotationCols
    with HasOutputAnnotationCol {

  /** Shape of annotations at output */
  private def outputDataType: DataType = ArrayType(Annotation.dataType)

  protected def wrapColumnMetadata(col: Column) = {
    val metadataBuilder: MetadataBuilder = new MetadataBuilder()
    metadataBuilder.putString("annotatorType", annotatorType)
    col.as(getOutputCol, metadataBuilder.build)
  }

  /** Override for additional custom schema checks */
  protected def extraValidateMsg = "Schema validation failed"
  protected def extraValidate(structType: StructType): Boolean = {
    true
  }

  /** requirement for pipeline transformation validation. It is called on fit() */
  override final def transformSchema(schema: StructType): StructType = {
    require(extraValidate(schema), extraValidateMsg)
    val metadataBuilder: MetadataBuilder = new MetadataBuilder()
    metadataBuilder.putString("annotatorType", annotatorType)
    val outputFields = schema.fields :+
      StructField(getOutputCol, outputDataType, nullable = false, metadataBuilder.build)
    StructType(outputFields)
  }


  /** requirement for annotators copies */
  override def copy(extra: ParamMap): M = defaultCopy(extra)


}
