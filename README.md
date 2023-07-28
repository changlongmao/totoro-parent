# totoro-parent

包含totoro-core核心依赖代码

以及totoro-generator代码生成器，包含controller, service, serviceImpl, mapper, mapper.xml,
entity,dto,vo

使用方式：

1. 将项目代码下载到本地

   `git clone git@github.com:changlongmao/totoro-parent.git`
2. 将代码安装到maven本地仓库

   `mvn clean install`

3. 手动新建一个springboot项目，修改pom.xml文件配置

```apache
    <parent>
        <groupId>org.totoro</groupId>
        <artifactId>totoro-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
  
    <dependencies>
        <dependency>
            <groupId>org.totoro</groupId>
            <artifactId>totoro-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.totoro</groupId>
            <artifactId>totoro-generator</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

主要作用为继承totoro-parent项目，而totoro-parent项目已经继承了spring-boot-starter-parent项目，即可使用springboot相关配置。

项目引用totoro-core核心依赖，里面包含了各种工具类，redisson缓存锁拦截器，防止sql注入，全局异常拦截，线程池，excel导入导出等功能。

totoro-generator为代码生成器，为使用了velocity模板引擎生成代码的方式

可在test路径下创建代码生成启动类，根据需要自行修改配置，启动main方法即会生成对应的代码

```apache
public class GeneratorCodeBoot {

    public static void main(String[] args) {
        // 当前项目路径
        String home = System.getProperty("user.dir");
        // 数据库连接配置，必须有
        BaseConfig baseConfig = BaseConfig.builder()
                .jdbcUrl("jdbc:mysql://127.0.0.1:3306/changlf?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai")
                .userName("root")
                .password("123456")
                // 指定生成的表，非必填，为空代表该数据库下所有表
                .tables(Sets.newHashSet("user"))
                .author("ChangLF")
                // 若为false，文件已存在则不生成
                .fileOverride(true)
                .packageConfig(PackageConfig.builder()
                        // java文件输出的父文件路径, 直接输出到项目代码中，请注意文件覆盖问题
                        .javaFileDir(home + "/src/main/java")
                        // java文件输出的父文件路径, 输出到外部/generator文件夹下，需自行拷贝代码
//                        .javaFileDir(home + "/generator")
                        .parentPackage("org.totoro.demo")
                        .entityPackage("entity")
                        .reqDTOPackage("javabean.dto")
                        .voPackage("javabean.vo")
                        .mapperPackage("mapper")
                        .servicePackage("service")
                        .serviceImplPackage("serviceImpl")
                        .controllerPackage("controller")
                        // 独立于javaFileDir
                        .mapperXmlDirectoryPath(home + "/src/main/resources/mapper")
                        .build())
                .build();
        // entity配置，必须有
        EntityConfig entityConfig = EntityConfig.builder()
                .logicDeleteColumnName("is_delete")
                .logicDeletePropertyName("deleteFlag")
                .build();
        // mapper配置，必须有
        MapperConfig mapperConfig = MapperConfig.builder()
                .build();
        // 可没有，若没有则不生成对应代码
        ServiceConfig serviceConfig = ServiceConfig.builder()
                .build();
        // 可没有，若没有则不生成对应代码
        ControllerConfig controllerConfig = ControllerConfig.builder()
                .build();

        // 生成代码
        GeneratorProcessor.process(baseConfig, entityConfig, mapperConfig, serviceConfig, controllerConfig);
    }

}
```

具体使用的范例在[totoro-demo](https://github.com/changlongmao/totoro-demo)项目中实现
