package xin.xingk.www.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import xin.xingk.www.mybatis.config.CustomMetaObjectHandler;
import xin.xingk.www.mybatis.config.CustomerIdGenerator;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Author: 陈靖杰
 * Date: 2022/2/14 09:29
 * Description: MybatisPlusUtil
 */
@Slf4j
public class MybatisPlusUtil {
    //SQL会话工厂
    public static SqlSessionFactory sqlSessionFactory;
    //SqlSession
    public static SqlSession sqlSession;

    /**
     * 初始化 SqlSessionFactory
     */
    static  {
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        MybatisConfiguration configuration = initConfiguration();
        //解析mapper.xml文件
        /*try {
            registryMapperXml(configuration, "mapper");
        } catch (IOException e) {
            log.error("解析mapper.xml文件发生异常{}",e.getMessage());
        }
        */
        sqlSessionFactory = builder.build(configuration);
     }

    /**
     * 获取当前 SQLSession
     */
    public static void getSqlSession(){
        sqlSession = sqlSessionFactory.openSession();
    }

    /**
     * 获取mapper对象
     * @param mapper mapper对象
     * @param <T>
     * @return
     */
    public static <T> T getMapper(Class<T> mapper){
        return sqlSession.getMapper(mapper);
    }

    /**
     * 关闭当前 SQLSession
     */
    public static void closeSqlSession() {
        sqlSession.commit();
        sqlSession.close();
    }

    /**
     * 初始化配置
     * @return MybatisConfiguration
     */
    private static MybatisConfiguration initConfiguration() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        //给configuration注入GlobalConfig里面的配置
        GlobalConfigUtils.setGlobalConfig(configuration, getGlobalConfig());
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setUseGeneratedKeys(true);
        configuration.addInterceptor(initInterceptor());
        //扫描mapper接口所在包
        configuration.addMappers("xin.xingk.www.mapper");
        //配置日志实现
        configuration.setLogImpl(Slf4jImpl.class);
        //设置数据源
        Environment environment = new Environment("1", new JdbcTransactionFactory(), initDataSource());
        configuration.setEnvironment(environment);
        return configuration;
    }

    public static GlobalConfig getGlobalConfig() {
        //构建mybatis-plus需要的GlobalConfig
        GlobalConfig globalConfig = new GlobalConfig();
        //初始化数据库数据库相关配置防止空指针
        globalConfig.setDbConfig(new GlobalConfig.DbConfig());
        //此参数会自动生成实现baseMapper的基础方法映射
        globalConfig.setSqlInjector(new DefaultSqlInjector());
        //设置id生成器
        globalConfig.setIdentifierGenerator(new CustomerIdGenerator());
        //sql字段填充器
        globalConfig.setMetaObjectHandler(new CustomMetaObjectHandler());
        //设置超类mapper
        globalConfig.setSuperMapperClass(BaseMapper.class);
        return globalConfig;
    }

    /**
     * 初始化数据源
     *
     * @return DataSource
     */
    private static DataSource initDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:sqlite:E:\\用户目录\\桌面\\init.db");
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setIdleTimeout(60000);
        dataSource.setAutoCommit(true);
        dataSource.setMaximumPoolSize(1);
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
    private static Interceptor initInterceptor() {
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
    private static void registryMapperXml(MybatisConfiguration configuration, String classPath) throws IOException {
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


}
