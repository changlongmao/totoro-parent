select t1.table_name,
       t2.table_comment,
       t1.column_name,
       t1.data_type,
       t1.column_type,
       t1.column_comment,
       t1.column_key,
       t1.is_nullable,
       t1.column_default
from information_schema.columns t1
inner join information_schema.tables t2
where t2.table_schema = t1.table_schema
  and t2.table_name = t1.table_name
  and t1.table_schema = (SELECT database())
  and ${tableNameQuery}
order by t1.table_name, t1.ordinal_position;