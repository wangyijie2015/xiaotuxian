演示流程：
1.mapreduce
先启动jps: 给看到5个进程，说明集群搭建好了
然后跑mapreduce任务：hadoop jar bike.jar /opt/bike.BikeDataAnalysis /input/hour.csv /output/
成功运行后：hdfs dfs -cat /output/part-r-00000 给看正确的运行结果
到浏览器查看：192.168.52.128:50070/

2.进入hive中，
desc bike_rental 看建的表结构
跑命令：SELECT dteday, SUM(cnt) AS total_rentals  
FROM bike_rental  
GROUP BY dteday;  每一天的租借次数：

3.flink:
直接看idea代码： 从hdfs上读取数据分析,这个指标的含义是计算每个季节和天气条件下的总计数。
然后给看发的跑的截图结果
进入到虚拟机，给看flink的web页面   192.168.52.128:8081/，就是提交到这上面运行

4.susperset：
进入浏览器：192.168.52.128:8787/
直接那个结果给看看就行
要可视化可以 用 其他人电脑，将结果导入到 然后图标就行 

演示就完成了

docker run -d --name mysql -p 3306:3306 -e TZ=Asia/Shanghai -e MYSQL_ROOT_PASSWORD=123456 mysql

docker network create heima #创建自己的网络环境

dockerCompose:通过一个单独的docker-compose.yml模板文件来定义一组相关联的应用容器，来实现多个相关联的Docker容器的快速部署。
dockerCompose可以完全描述docker run的内容

docker重新运行命令：
docker restart 8b8d151d7853ab1110827e2f9e8ddbf0f92716a42693cc0a2f3576fe130ec4b6

拉取共享配置：
基于NacosConfig拉取共享配置代替微服务的本地配置：加载bootstrap.yml-->拉取Nacos配置-->初始化ApplicationContext

配置热更新：当修改配置文件中的配置时，微服务无需重启即可使配置生效
雪崩问题产生的原因：
	1.微服务相互调用，服务提供者出现故障或阻塞
	2.服务调用者没有做好异常处理，导致自身故障
	3.调用链中的所有服务级联失败，导致整个集群故障
解决问题的思路有哪些？
	尽量避免服务出现故障或阻塞：请求限流，限制访问微服务的请求的并发量，避免服务因流量激增出现故障	
	保证代码的健壮性
	保证网络畅通
	能应对较高的并发请求

线程隔离：也称为舱壁模式,模拟船舱隔板的防水原理吗,通过限定每个业务能使用的线程数量而将故障业务隔离，避免故障扩散。

熔断期间，所有请求快速失败，全部都走fallback逻辑。

解决雪崩问题的常见方案有哪些：
	请求限流
	线程隔离：控制业务可用的线程数量，将故障隔离在一定范围内。
异步调用：
	异步调用通常基于消息通知的方式,包含了三个角色。
	消息发送者：投递消息的人,
	
SpringAMQP提供了几个类:Queue:声明队列，Exchange:声明交换机 Binding:用于声明队列和交换机的绑定关系。




using BCVP.Net8.IService;
using BCVP.Net8.Model;
using BCVP.Net8.Repository;

namespace BCVP.Net8.Service
{
    public class UserService : IUserService
    {
        public async Task<List<UserVo>> Query()
        {
           var userRepo = new UserRepository();
           var users = await userRepo.Query();
            return users.Select( d => new UserVo(){ UserName = d.Name }).ToList();
        }
    }
}

.net8的日志记录包括：
log4net和日志组件NLog两种

多种注册方式、
1.多生命周期的支持
2.别名的支持

Autofac: 性能高，功能更加强大
得到容器的最终实例：注册程序集
注册程序集的方式：

通过接口的方式来完成AOP扩展 特点：只要是实现了这个接口的类，内部的所有的方法调用都会走AOP流程
EFCore：ORM框架，OOP思想完成对于数据库的操作


LangChain是一个用于开发由语言模型驱动的应用程序的框架
Components:为LLMs提供接口封装，模板提示和信息检索索引
Chains：将不同的组件组合起来解决特定的任务，比如在大量文本中查找信息
Agents: 是的LLMs能与外部环境进行交互，如通过API请求执行操作
向量数据库VectorStore是数据表示和检索的手段，为模型提供必要的理解和支持。
LangSmith

1.创建模型
2.准备prompt
3.创建返回的数据解析器
4.得到链
chain = model | parser

5.直接使用chain来调用，然后直接打印

使用PromptTemplate:提示模板
使用LangSimish来追踪你部署的应用程序

Langchian构建向量数据库和检索器
 支持从向量数据库和其他来源检索数据，以便于和LLM工作流程进行集成。就像检索增强生成（RAG)的情况是一样的

vector_store.similarity_search_with_socre() :相似度的查询：返回相似的分数，分数越低则相似度越高

# 检索器：bind(k=1) 返回相似度最高的第一个

retriever = RunnableLambda(vector_store.similarity_search).bind(k=1)

retriever.bacth()

langchian构建代理 返回到代理中

tavily搜索引擎作为工具，max_result:只返回两个结果

RAG是一种增强大语言模型（LLM)知识的方法

Langchian构建RAG的对话应用实现思路：
1.加载：首席按，加载数据，通过DocumentLoaders完成
2.分割：Tesxt splitters将 大型文档分割成更小的块，对于索引数据和将其传递给大模型很有用
3.存储：需要一个地方来存储和索引我们的分割，以便以后可以搜索，通常使用VectorStore和Embeddings模型完成
4.检索：给定用户输入，使用检索器从存储中检索相关分割
5.生成：ChatModel/LLM使用包括问题和检索到的数据的提示生成答案

Langchain
1.将问题转换为DSL查询：模型将用户输入转换为SQL查询
2.执行SQL查询
3.回答问题：模型使用查询结果相应用户输入

保存向量数据库
加载向量数据库

pydantic: 处理数据，验证数据，定义数据的格式，序列化数据和反序列化，类型转换等等

文本分类：将文本树自动归类到预定义的类别中

Data-Juicer是一个一站式的多模态处理系统

使用ms-swift训练模型
编写训练的脚本：命令行参数参看：https://swift.readthedocs.io/zh-cn/latest/LLM/命令行参数.html
https://swift.readthedocs.io/zh-cn/latest/Instruction/%E5%91%BD%E4%BB%A4%E8%A1%8C%E5%8F%82%E6%95%B0.html
需要注意的参数有：
1.dataset可以混用一些通用数据集，防止模型灾难性遗忘和通用能力丢失
2.system可以设置一个符合任务特性的system prompt,提升模型能力
3.lora_target_modules可以根据训练任务的难易程度，调整可以训练的参数数量

lora 微调
dataset_test_ratio 0.01 抽出部分数据用于测试
lora_target_modules  ALL 加载在那些层里
dtype bf16
max_length 2048 长度
save_total_limit 

6千条，可能需要10-20min
adapter_model.safetensores 训练的权重

模型评估：使用evalscope评估模型
EvalScope是一个LLM/VLM评估框架，预置了多个常用的测试基准，实现了多种常用评估指标，提供了直观的评估结果展示，支持和ms-swift的无缝集成。
使用general qa模板自定义评估数据集
评估指标：
blue:比较生成文本和参看文本中的n-gram(n个连续单词的序列），常用的n有1（unigram),2(bigram),3(trigram)等等
rouge:侧重于召回率（recall)

数据格式：
需要query和response两个字段，例如

写评估配置文件
目前支持general_qa和ceval两种pattern:custom_eval_config.json

ceval为多选题
 通过模型的微调，使其更与提供的测试集相匹配，做到更好的效果
 加大模型的温度系数，减少重复数据的生成

模型上传：
  使用modelscope modelhub来将训练好的模型上传到ModelScope平台。可以提前在ModelScope社区网页创建对应的模型，然后将本地模型通过
push_model接口进行上传，也可以通过push_model自动完成模型的创建和上传
  






