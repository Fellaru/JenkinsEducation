- [Что такое CI](https://habr.com/post/219891/) //

- [Изумительная статья для начала](https://habr.com/post/353194/)

Наиболее яркие примеры систем CI:
- Jenkins
- Hudson
- TeamCity
- Atlassian Bamboo
- Travis


Прочитать что такое Ansible



Jenkins
  [Управление задачами в Jenkins](https://habr.com/company/selectel/blog/339390/)
  
  Дополнительно:
  [Тестирование кода написаного для CI Molecule](https://habr.com/post/351974/)
  
  
  
  [Самое важное! Jenkins Get Started](https://jenkins.io/doc/pipeline/tour/getting-started/)
  
  
  Откуда стоит начинать изучение Jenkins GetSterted :
  1. [Начнем с GettingStarted](https://jenkins.io/doc/pipeline/tour/getting-started/)
  1. [Создадим задачу и поймем как ее конфигурируют](Это есть и в Get Started Pipeline и в https://www.youtube.com/watch?v=89yWXXIOisk&list=PLhW3qG5bs-L_ZCOA4zNPSoGbnVQ-rp_dG )
  1. [Основы JEnkins для разработчиков. изучаем Pipeline](https://jenkins.io/doc/book/pipeline/getting-started/)
  
  
** ** ** **  
  
#Краткий перевод на русский [Using a Jenkinsfile](https://jenkins.io/doc/book/pipeline/jenkinsfile/)
 
** ** ** **  
   
Хорошая практика в Jennkins - Создание Jenkinsfile, который проверяется(ищется)в source control

Pipeline поддерживает два вида синтаксиса:
1. Declarative(pipeline, stages, stage, step)
1. Scripted(node, stage)



##Созднаие Jenkinsfile
**agent**- обязательная директива в Jenkinsfile, инструктирует Jenkins выделить executor'а и workspace для Pipeline.
 Без директивы **agent** Declarative Pipeline не просто не валдный, а не способный выполнить какую-либо работу.
 По умолчанию директива **agent** гарантирует, что source repository будет checked out и будет доступен для шагов 
 на последующих этапах
 
 
 
Директивы **stages** и директивы **steps** также требуются(обязательны) для действительного Declarative Pipeline,
поскольку они инструктируют Jenkins, что выполнить, и на каком этапе он должен это выполнить.

** ** ** 

Для более продвинутого использования с Scripted Pipeline, **node** - это ключевой первый шаг, поскольку он выделяет
executor'а и workspace для Pipeline. В сущности без  **node** Pipeline не может сделать что-либо.

Изнутри **node** первым делом будет проверка(checkout) исходного кода(source code) для этого проекта. Поскольку Jenkinsfile вытаскивается
непосредственно из source control, Pipeline обеспечивает быстрый и простой способ доступа к правильной ревизии(revision)
 исходного кода(source code). Для этого используется код:
 
 ```
 node {
     checkout scm 
     /* .. snip .. */
 }
```

 **checkout** шаг для checkout код из source control
 **scm** -это специальная переменная, которая инструктирует checkout этап, чтобы клонировать конкретную ревизию(revision),
  которая вызвала запуск этого конвейера(Pipeline).
  
###Build
Обычно это этап Pipeline будет там, где исходный код собирается, компилируется или упаковывается.
Jenkinsfile не является заменой существующего инструмента построения, такого как GNU / Make, Maven, Gradle и т. Д.,
Но может быть рассмотрен как слой клея, который связывает несколько этапов жизненного цикла разработки проекта 
(сборка, тестирование, развертывание и т. Д.) вместе.

У Jenkins есть несколько плагинов для вызова практически любого инструмента построения(build tool) в общем использовании.

**sh** - выполнение команды в консоли системы Unix / Linux
**bat** - выполнение команды в консоли системы Windows

 
//TODO Надо бы найти как запускать clean install  из консоли с помощью gradle и maven и законспектировать это

**archiveArtifacts** выберает файлы соответствующие шаблону (в переменной artifacts метода, пример **/target/\*.jar)
  и сохраняет их в Jenkins master для последующего поиска.
  
###Test  
Выполнение автоматизированных тестов является важнейшим компонентом любого успешного процесса continuous delivery.
Таким образом, у Jenkins есть ряд тестовых средств записи, отчетности и визуализации, предоставляемых рядом плагинов(ищите, скачивайте)


На фундаментальном уровне, когда есть сбои в тестировании, полезно, чтобы Дженкинс записывал сбои для отчетности и визуализации
в веб-интерфейсе. В приведенном ниже примере используется шаг **junit**, предоставляемый плагином JUnit.

В пример ниже, если тесты терпят неудачу, Pipeline отмечен как «неустойчивый», как обозначается желтым шаром в веб-интерфейсе.
Основываясь на зарегистрированных отчетах об испытаниях, Дженкинс может также обеспечить анализ и визуализацию исторического тренда.

###Deploy
Развертывание(Deployment) может подразумевать различные этапы, в зависимости от требований проекта или организации,
и может быть каким угодно: от публикации встроенных артефактов до сервера Artifactory, до deploy(заливания/подталкивания)
кода в производственную систему(production).

На этом этапе примера Pipeline успешно завершили стадии «Build» и «Test». По сути, этап «Deploy» будет выполняться только
в том случае, если предыдущие этапы завершатся успешно, иначе pipeline exited(кокнчится выйдет) раньше.(Этот щаг дальше 
не выполняется, следующие шаги выполняются)

```
pipeline {
    agent any

    stages {
        stage('Deploy') {
            when {
              expression {
                currentBuild.result == null || currentBuild.result == 'SUCCESS' 
              }
            }
            steps {
                sh 'make publish'
            }
        }
    }
}

```
** ** **
Scripted Pipeline может включать в себя условные(conditional ) тесты (показано выше), циклы, блоки try / catch / finally
и даже функции. В следующем разделе более подробно рассказывается об этом расширенном Scripted Pipeline syntax.

##Working with your Jenkinsfile

В следующих разделах приведены сведения об обработке:
 - конкретный синтаксис Pipeline в вашем Jenkinsfile  и
 - функции и функциональность синтаксиса Pipeline, которые необходимы для создания вашего приложения или проекта Pipeline
 
###Работа со строками
в Jenkins Pipeline со строками работают так же как и в Groovy
'' - фиксированная строка(Java строка), не работает с переменными
"" - Groovy строка, можно работать с переменными(используя ${} )

###Использование переменных среды(environment variables)
Jenkins Pipeline предоставляет переменные среды через глобальную переменную env, которая доступна из любого места внутри
Jenkinsfile. Полный список переменных окружения, доступных из Jenkins Pipeline, документирован в localhost:8080/pipeline-syntax/globals#env,
И этот список включает в себя:
BUILD_ID

JOB_NAME

JENKINS_URL
Полный URL-адрес Jenkins, например example.com:port/jenkins/ (ПРИМЕЧАНИЕ: доступно только в том случае, если URL-адрес Jenkins установлен в «Конфигурация системы»)

``` 
pipeline {
    agent any
    stages {
        stage('Example') {
            steps {
                echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
            }
        }
    }
}
```

//TODO Судя по тому что я прочла environment variables доступны только для ipeline Jenkins syntax, а вот для Script нет
почетать об этом