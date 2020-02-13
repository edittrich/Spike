import logging

from pyspark.sql import SQLContext, SparkSession

logging.basicConfig(format='%(asctime)s - %(levelname)s - %(message)s', level=logging.INFO)


def main():
    try:
        app_name = "Spike PySpark"
        db_name = "spikepyspark"
        table_name = "test"

        sc = SparkSession.builder. \
            appName(app_name). \
            master("local[2]"). \
            enableHiveSupport(). \
            getOrCreate()

        sql_context = SQLContext(sc)
        logging.info('SQLContext available...')

        headers = ("id", "some_text")
        data = [
            (1, "all_lower_case_1"),
            (2, "asljkwq3j4flqkje342"),
            (3, "it was the best of times, it was the worst of times"),
            (4, "May the fourth be with you"),
            (5, "The fifth element"),
            (6, "all_lower_case_1"),
        ]

        df = sql_context.createDataFrame(data, headers)
        df.show()

        df = sql_context.read.csv("./../test.csv", header='true')
        sc.sql("create database if not exists " + db_name)
        df.write.mode("overwrite").format("orc").saveAsTable(db_name + "." + table_name)
        df = sc.sql("select * from " + db_name + "." + table_name + " where Stunde = 1")
        df.show()
        df.explain()

    except Exception as e:
        logging.error("Exception occurred", exc_info=True)


main()
