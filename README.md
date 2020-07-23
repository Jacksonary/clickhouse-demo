# clickhouse-demo

参考文档：

* [quick-start](https://clickhouse.tech/#quick-start).

## 1. 部署

clickhouse版本为 20.4.6。

```shell script
# 1. 部署
yum install yum-utils
rpm --import https://repo.clickhouse.tech/CLICKHOUSE-KEY.GPG
yum-config-manager --add-repo https://repo.clickhouse.tech/rpm/clickhouse.repo
sudo yum install clickhouse-server clickhouse-client

# 2. 启动并登录
# 2.1 启动服务
/etc/init.d/clickhouse-server start
# 2.2 开启客户端，全命令为 clickhouse-client –h host –u –p
clickhouse-client
```

注：

* 默认端口号：TCP端口 9000，HTTP连接端口 8123；
* 默认用户：`default`，默认密码：无，这个账号有所有的权限，但是不能使用SQL驱动方式的访问权限和账户管理；

## 2. 开启远程连接

&emsp;clickhouse的配置文件为`/etc/clickhouse-server/config.xml`和`/var/lib/clickhouse/`目录，按照如下步骤操作：

1. 放开注释（大概72行）`<listen_host>::</listen_host>`；
2. 重启服务 `service clickhouse-server restart`；
3. 检查服务是否正常启动： `service clickhouse-server status`；

如果启动异常，如：
```log
Start clickhouse-server service: already running FAILED
Application: DB::Exception: Listen [::]:8123 failed: Poco::Exception. Code: 1000, e.code() = 0, e.displayText() = DNS error: EAI: -9
```

将上述的1步骤丢弃，而是将配置中的`<listen_host>0.0.0.0</listen_host>`放开注释重启即可。

## 3. 可视化工具 DBeaver

&emsp;说实话没有找到类似于Navicat比较友好的工具，所以迫不得已选择了DBeaver，基本的连接信息就不多说了，端口是 8123，如果在下载 jdbc 驱动出现问题（如：连接clickHouse出现驱动未找到解决方案），可以添加一下阿里云的镜像仓库可以拉取，通过窗口中的菜单栏可以解决：窗口 --> 首选项 --> DBeaver --> 驱动 --> maven --> 添加阿里云仓库`http://maven.aliyun.com/nexus/content/groups/public/`。


>注：如果用它的可视化操作提示报错，就手写SQL，该工具的某些可视化操作自动生成的SQL语句确实是错误的，比如新增列，自动生成的SQL为`ALTER TABLE goujianwu.shop ADD test String(100);`，而正确的SQL应为`ALTER TABLE goujianwu.shop ADD COLUMN test String;`

## 4. SQL整理

&emsp;总结了下常用的几个SQL：

### 4.1 数据库

```sql
-- 创建数据库
CREATE DATABASE [IF NOT EXISTS] db_name [ON CLUSTER cluster];
-- 示例
CREATE database test_database;
```

此外，clickhouse支持外挂表，比如将mysql的某个库外挂到clickhouse中：

```sql
CREATE DATABASE [IF NOT EXISTS] db_name [ON CLUSTER cluster]
ENGINE = MySQL('host:port', ['database' | database], 'user', 'password')

-- 示例
CREATE DATABASE test_mysql
ENGINE = MySQL('10.5.3.28:3306', 'test_in_mysql', 'admin', 'admin')
```
注：只允许对表进行`INSERT`和`SELECT`

### 4.2 表

#### 4.2.1 表创建

```sql
-- 创建表，推荐 MergeTree 引擎：
CREATE TABLE [IF NOT EXISTS] [db.]table_name ON CLUSTER cluster
(
    name1 [type1] [DEFAULT|MATERIALIZED|ALIAS expr1],
    name2 [type2] [DEFAULT|MATERIALIZED|ALIAS expr2],
    ...
    INDEX index_name1 expr1 TYPE type1(...) GRANULARITY value1,
    INDEX index_name2 expr2 TYPE type2(...) GRANULARITY value2
) ENGINE = engine_name()
[PARTITION BY expr]
[ORDER BY expr]
[PRIMARY KEY expr]
[SAMPLE BY expr]
[SETTINGS name=value, ...];

-- 示例，Nullable(column) 表示该字段允许为空
CREATE TABLE test_database.job
(
    `id` Int64,
    `type` Nullable(String),
    `source_id` Nullable(Int64),
    `source_name` Nullable(String),
    `status` Nullable(Int8),
    `input_parameter` Nullable(String),
    `result_id` Nullable(Int64),
    `message` Nullable(String),
    `databag_id` Nullable(String),
    `create_time` DateTime DEFAULT now(),
    `update_time` Nullable(DateTime)
) ENGINE = MergeTree
PARTITION BY toYYYYMM(create_time)
PRIMARY KEY id
ORDER BY (id,create_time)
SETTINGS index_granularity = 8192;
```

**参数描述**
* `db`：指定数据库名称，如果当前语句没有包含‘db’，则默认使用当前选择的数据库为‘db’。
* `cluster`：指定集群名称，目前固定为`default`，`ON CLUSTER`将在每一个节点上都创建一个本地表。
* `type`：该列数据类型，例如 `UInt32`。
* `DEFAULT`：该列缺省值。如果`INSERT`中不包含指定的列，那么将通过表达式计算它的默认值并填充它。
* `MATERIALIZED`：物化列表达式，表示该列不能被INSERT，是被计算出来的； 在INSERT语句中，不需要写入该列；在`SELECT`查询语句结果集不包含该列。
* `ALIAS`：别名列。这样的列不会存储在表中。 它的值不能够通过`INSERT`写入，同时使用`SELECT`查询星号时，这些列也不会被用来替换星号。 但是它们可以用于`SELECT`中，在这种情况下，在查询分析中别名将被替换。
* 物化列与别名列的区别： 物化列是会保存数据，查询的时候不需要计算，而表达式列不会保存数据，查询的时候需要计算，查询时候返回表达式的计算结果。

**表引擎相关参数**(只有MergeTree系列表引擎支持)
* `PARTITION BY`：指定分区键。通常按照日期分区，也可以用其他字段或字段表达式。
* `PRIMARY KEY`：指定主键，默认情况下主键跟排序键相同。因此，大部分情况下不需要再专门指定一个`PRIMARY KEY`子句。
* `ORDER BY`：指定排序键。可以是一组列的元组或任意的表达式，必须和`PRIMARY KEY`的值的相对顺序保持一致，如`PRIMARY KEY(create_time,module) ORDER BY(module,create_time)`就是非法的，`ORDER BY`只能是`create_time,module`（顺序）或者是2个中的一个。
* `SAMPLE BY`：抽样表达式，如果要用抽样表达式，主键中必须包含这个表达式。
* `SETTINGS`：影响 性能的额外参数。
* `GRANULARITY`：索引粒度参数。

```sql
-- 删除数据库/表
DROP DATABASE [IF EXISTS] db [ON CLUSTER cluster]
```


#### 4.2.2 数据的增、删、改、查

&emsp;表结构以及数据本身的操作如下：

```sql
-- 查询
SELECT * FROM job WHERE id = 123;

-- 插入
INSERT INTO job (id, type, source_id) values(1, 'test-type', 6599865714596938290);

-- 更新
ALTER TABLE job
UPDATE type='test-type2', source_id=33
WHERE id = 123;

-- 删除
ALTER TABLE job
DELETE
WHERE id = 123;


-- 新增列
ALTER TABLE job ADD COLUMN test String DEFAULT 1;
-- 删除列
ALTER TABLE job DROP COLUMN test;
-- 修改列属性的值类型
ALTER TABLE job MODIFY COLUMN update_time DateTime64;
-- 修改注释
ALTER TABLE job COMMENT COLUMN test 'The table shows the test used for accessing the site.';
```

### 4.3 数据同步

&emsp;这里是指从MySQL中同步数据，方式为：

```sql
-- 先创建表再导入数据，前提是在clickhouse中先创建好表，推荐
INSERT INTO test_database.mysql_table SELECT * FROM mysql('<host>:<port>', 'test_db', 'mysql_test_table', '<username>', '<password>');

-- 直接复制表和数据，注，这种方式只保证数据会过来，2种数据库之间的结构转换会有些问题，比如默认值会丢
CREATE TABLE test_database.mysql_table ENGINE = MergeTree ORDER BY id AS SELECT * FROM mysql('<host>:<port>', 'test_db', 'mysql_test_table', '<username>', '<password>');
```

## 5. java-client 以及Springboot的集成

&emsp;这里采用的官方的[clickhouse-jdbc](https://github.com/ClickHouse/clickhouse-jdbc)，和 MySQL 的jdbc操作类似，然后就是在 Springboot 整合使用见[clickhouse-demo](https://github.com/Jacksonary/clickhouse-demo)，同样采用MyBatis和mybatis-generator做支撑，但综合发现支持相对不怎么友好，只能生成一半后，自己改改了，详细操作见[mapper文件夹](https://github.com/Jacksonary/clickhouse-demo/tree/master/src/main/resources/org/example/dao/mapper)。

注：这里注意时间类型不能对应上，在具体sql的字段上应使用clickhouse内置函数`toDateTime(date)`、`toDate(date)`函数将java中的Date作一层转换才可以，否则会报错。

【附】
clickhouse中几种常用基本数据类型：

* 整型：
    * 有符号整型：`Int8`,`Int16`,`Int32`,`Int64`；
    * 无符号整型：`UInt8`,`UInt16`,`UInt32`,`UInt64`；
* 浮点数：`Float32`、`Float64`；
* 字母：`String`(长度不限)、`FixedString(N)`(定长字符串,当长度小于 N 时，在字符串末尾添加空字节来达到 N 字节长度，大于 N 时，将返回错误)；
* 数组：`Array(T)`，T 可以是任意类型，包含数组类型，如`Array(UInt8)`，但不推荐使用多维数组，ClickHouse 对多维数组的支持有限；
* 元组：每个元素都有单独的类型，`Tuple(T1, T2, ...)`，如`Tuple(Int8, String, Array(String), Array(Int8))`；
* 日期：`Date`，2个字节，形如`1970-01-01`；
* 时间戳：`DateTime`，4个字节，精确到秒，形如`0000-00-00 00:00:00`。`DateTime64`，可以精确到`2020-01-01 05:00:01.000`；
* 域类型：ipv4/6的数据类型可以在为紧凑的二进制存储的同时支持识别可读性更加友好的输入输出格式
    * IPV4：即`IPV4`类型；
    * IPV6：即`IPV6`类型；
* 可为空：`Nullable(Int8)`，这个数据类型一般是和其他数据类型嵌套使用，表示某种类型的字段可以为`NULL`；
* UUID：`UUID`类型，内置函数`generateUUIDv4()`可以生成`UUID`类型的值；
* 枚举：`Enum8`（-128 ... 127）和 `Enum16`（-32768 ... 32767），通常是定义一些key-value形式的值，比如`test Enum8('hello' = 1, 'world' = 2)`，那 test 字段只能存`hello`和`world`中的某个，默认枚举不能为空（如有需要可以用`NULLable`改变）。查询时枚举类型的值为之前定义的`hello`/`world`。

注：clickhouse中没有什么Byte和布尔值，只有Int。

【参考文档】
>https://clickhouse.tech/docs/en/
>https://github.com/ClickHouse/clickhouse-jdbc
>https://programmer.help/blogs/5d9d204350ade.html
>https://www.cnblogs.com/huanghanyu/p/12895592.html
>https://help.aliyun.com/document_detail/146116.html?spm=a2c4g.11186623.6.586.206110454hgEWI
> 配置多数据源参考文档：https://www.jianshu.com/p/ff5af6c59365?utm_source=oschina-app