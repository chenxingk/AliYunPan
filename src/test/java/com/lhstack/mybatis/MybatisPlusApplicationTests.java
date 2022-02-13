package com.lhstack.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhstack.mybatis.entity.User;
import com.lhstack.mybatis.mapper.UserMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@RunWith(JUnit4.class)
public class MybatisPlusApplicationTests {

    private SqlSession session;

    @Before
    public void before() throws IOException {
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        MybatisConfiguration configuration = new MybatisConfiguration();
        //给configuration注入GlobalConfig里面的配置
        GlobalConfigUtils.setGlobalConfig(configuration, getGlobalConfig());
        initConfiguration(configuration);
        this.registryMapperXml(configuration, "mapper");
        //构建sqlSessionFactory
        SqlSessionFactory sqlSessionFactory = builder.build(configuration);
        this.session = sqlSessionFactory.openSession();
    }

    public GlobalConfig getGlobalConfig() {
        //构建mybatis-plus需要的globalconfig
        GlobalConfig globalConfig = new GlobalConfig();
        //初始化数据库数据库相关配置防止空指针
        globalConfig.setDbConfig(new GlobalConfig.DbConfig());
        //此参数会自动生成实现baseMapper的基础方法映射
        globalConfig.setSqlInjector(new DefaultSqlInjector());
        //设置id生成器
        globalConfig.setIdentifierGenerator(new CustomerIdGenerator());
        //全局处理器
        globalConfig.setMetaObjectHandler(new DataAutoFill());
        //设置超类mapper
        globalConfig.setSuperMapperClass(BaseMapper.class);
        return globalConfig;
    }

    @After
    public void after() {
        this.session.commit();
        this.session.close();
    }

    /**
     * 初始化配置
     *
     * @param configuration
     */
    private void initConfiguration(MybatisConfiguration configuration) {
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setUseGeneratedKeys(true);
        configuration.addInterceptor(initInterceptor());
        //扫描mapper接口所在包
        configuration.addMappers("com.lhstack.mybatis.mapper");
        //配置日志实现
        configuration.setLogImpl(Slf4jImpl.class);
        //设置数据源
        Environment environment = new Environment("1", new JdbcTransactionFactory(), initDataSource());
        configuration.setEnvironment(environment);
    }

    /**
     * 初始化数据源
     *
     * @return
     */
    private DataSource initDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:sqlite:E:\\用户目录\\桌面\\2.db");
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setIdleTimeout(60000);
        dataSource.setAutoCommit(true);
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);
        dataSource.setMaxLifetime(60000 * 10);
        dataSource.setConnectionTestQuery("SELECT 1");
        return dataSource;
    }

    /**
     * 初始化拦截器
     *
     * @return
     */
    private Interceptor initInterceptor() {
        //创建mybatis-plus插件对象
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //构建分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.SQLITE);
        paginationInnerInterceptor.setOverflow(true);
        paginationInnerInterceptor.setMaxLimit(500L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }


    /**
     * 解析mapper.xml文件
     *
     * @param configuration
     * @param classPath
     * @throws IOException
     */
    private void registryMapperXml(MybatisConfiguration configuration, String classPath) throws IOException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> mapper = contextClassLoader.getResources(classPath);
        while (mapper.hasMoreElements()) {
            URL url = mapper.nextElement();
            if (url.getProtocol().equals("file")) {
                String path = url.getPath();
                File file = new File(path);
                File[] files = file.listFiles();
                for (File f : files) {
                    FileInputStream in = new FileInputStream(f);
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, f.getPath(), configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                    in.close();
                }
            } else {
                JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                JarFile jarFile = urlConnection.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.getName().endsWith(".xml")) {
                        InputStream in = jarFile.getInputStream(jarEntry);
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, jarEntry.getName(), configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                        in.close();
                    }
                }
            }
        }
    }

    @Test
    public void testInsert() {
        User user = new User();
        user.setName("陈靖杰");
        user.setToken("6666666");
        UserMapper mapper = session.getMapper(UserMapper.class);
        mapper.insert(user);
        System.out.println(user);
    }

    @Test
    public void testSelectPage() {
        UserMapper mapper = session.getMapper(UserMapper.class);
        Page<User> pageResult = mapper.selectPage(new Page<>(2, 1), new QueryWrapper<>());
        System.out.println(pageResult.getTotal());
        System.out.println(pageResult.getPages());
        System.out.println(pageResult.getRecords());
    }

    @Test
    public void testDelete() {
        UserMapper mapper = session.getMapper(UserMapper.class);
        int result = mapper.deleteById(5);
        System.out.println(result);
        this.testSelectList();
    }

    @Test
    public void testUpdate() {
        UserMapper mapper = session.getMapper(UserMapper.class);
        User user = mapper.selectById(5);
        user.setName("test");
//        user.setUpdateTime(new Date());
//        user.setPassword("654321");
//        user.setDescription("update");
        mapper.updateById(user);
        System.out.println(user);
    }

    @Test
    public void testSelectList() {
        UserMapper mapper = session.getMapper(UserMapper.class);
        List<User> users = mapper.selectList(new QueryWrapper<>());
        System.out.println(users);
    }

    @Test
    public void testFindAll() {
        UserMapper mapper = session.getMapper(UserMapper.class);
        List<User> list = mapper.findAll();
        System.out.println(list);
    }

    @Test
    public void testFindAllPage() {
        UserMapper mapper = session.getMapper(UserMapper.class);
        this.testInsert();
        Page<User> pageResult = mapper.selectPage(new Page<>(1, 1),null);
        System.out.println(pageResult.getTotal());
        System.out.println(pageResult.getPages());
        System.out.println(pageResult.getRecords());
    }


    @Test
    public void testDataSource() throws Exception {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/cms?allowPublicKeyRetrieval=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&useUnicode=true");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setIdleTimeout(60000);
        dataSource.setAutoCommit(true);
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);
        dataSource.setMaxLifetime(60000 * 10);
        dataSource.setConnectionTestQuery("SELECT 1");
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("show table status");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String name = metaData.getColumnLabel(i);
                String field = resultSet.getString(i);
                System.out.printf("%s:%s\t", name, field);
            }
            System.out.println();
        }
    }

}


