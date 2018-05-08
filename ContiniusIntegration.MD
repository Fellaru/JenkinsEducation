- [Что такое CI](https://habr.com/post/219891/) //

- [Изумительная статья для начала](https://habr.com/post/353194/)

Наиболее яркие примеры систем CI:
- Jenkins
- Hudson
- TeamCity
- Atlassian Bamboo
- Travis


Прочитать что такое Ansible

[Сравнение Jenkins vs Travis CI](https://www.keycdn.com/blog/jenkins-vs-travis/)

Jenkins
  [Управление задачами в Jenkins](https://habr.com/company/selectel/blog/339390/)
  
  Дополнительно:
  [Тестирование кода написаного для CI Molecule](https://habr.com/post/351974/)
  
  
  
  [Самое важное! Jenkins Get Started](https://jenkins.io/doc/pipeline/tour/getting-started/)
  
  
  
  Ссылки найденные во время fix'ов
  1. https://wilsonmar.github.io/jenkins2-pipeline/
  1. https://github.com/jenkinsci/pipeline-examples/blob/master/declarative-examples/jenkinsfile-examples/mavenDocker.groovy
  1. https://github.com/jenkinsci/stashnotifier-plugin
  
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

Environment variables доступны как и для Declarative Pipeline Jenkins syntax,так и для Script Pipeline Jenkins syntax.

###настройка environment variables
- **environment** - для Declarative Pipeline
- **withEnv** - для Scripted Pipeline
Настройка переменных окружения в Jenkins Pipeline выполняется по-разному в зависимости от того, используется ли Declarative
или Scripted Pipeline.

Declarative Pipeline поддерживает директиву **environment**, тогда как пользователи Scripted Pipeline должны использовать шаг **withEnv**.

Пример в Declarative Pipeline
``` 
pipeline {
    agent any
    environment { 
        CC = 'clang' //1
    }
    stages {
        stage('Example') {
            environment { 
                DEBUG_FLAGS = '-g'//2
            }
            steps {
                sh 'printenv'
            }
        }
    }
}
```
- 1 (top-level pipeline block) изменения будут применять ко всем этапам в пределах Pipeline
- 2 (directive defined within a stage), изменения будут применяться

 Как я поняла Этот блок используется для настройки новых переменных
 а не для Изменения существующих

###Обработка учетных данных(Handling credentials)

####Для секретного текста, usernames и пароля, и секретных файлов
Jenkins' Declarative Pipeline синтакс имеет вспомогательный метод **credentials()**
(используется внутри директивы environment)

- Secret text
```
pipeline {
    agent {
     ..
    }
    environment {
        AWS_ACCESS_KEY_ID     = credentials('jenkins-aws-secret-key-id')
        AWS_SECRET_ACCESS_KEY = credentials('jenkins-aws-secret-access-key')
    }
    stages {
      ...
}
```
теперь мы можем использовать наши переменные в дальнейшем коде ссылаясь с помощью $имя_переменной, но если переменные
определенные с помощью метода credentials() попытаться вывести на консоль(например используя `echo $AWS_SECRET_ACCESS_KEY`)
Jenkins вернет только `****`


- Usernames and passwords
Устанавливая в JenkinsFile c помощью метода credentials(). Мы во первых получаем защиту данных(как вSecret text),
а во вторых
```
environment {
    COMMAND_COMMON_CREDS = credentials('username:password')
}
```

Определяю логин пароль в формате username:password ( с разделением через :) мы в действительности получаем аж 3 переменных
1. COMMAND_COMMON_CREDS (саму переменную)
1. COMMAND_COMMON_CREDS_USR (имяПеременой_USR - содержит только username)
1. COMMAND_COMMON_CREDS_PSW (имяПеременной_PSW -  содержит только password)

- Secret files
Секретные файлы обрабатываются точно так же, как секретный текст (При запросе на вывод содержимого получаем  `****`). 
По сути, единственное различие между секретным текстом и секретными файловыми учетными данными заключается в том, что 
для секретного текста сами учетные данные вводятся непосредственно в Jenkins, тогда как для секретного файла учетные 
данные первоначально хранятся в файле, который затем загружается в Jenkins.
В отличие от секретного текста, секретные файлы обслуживают учетные данные, которые или слишком громоздкие, чтобы 
напрямую вводить в Jenkins или в двоичном формате, таком как файл GPG
 
 - For other credential types
 [ссылка](https://jenkins.io/doc/book/pipeline/jenkinsfile/#for-other-credential-types)
 используйте Snippet Generator
 В  Jenkins UI
 1. Из домашней страницы Jenkins, нажмите на имя вашего Pipeline project/item
 1. Войдите в меню(что внутри задачи) в пункт **Pipeline Syntax** и окажитесь в **Snippet Generator**
 1. В поле Sample Step выберите withCredentials:Bind credentials to variables.
 1. В поле Bindings выберете нужное и после нажимем Generate Pipeline Script 
 1. Вставляем в нащ JenkinsFile сгенерированный текст  
 
 
###Параметры Обработки(Handling parameters)
Конфигурируя с помощью Declarative  Pipeline используем дирктиву **parameters** 
А с помощью Scripted Pipeline с помощью шага **properties**

Доступ к параметру осуществляется с помощью такого синтаксиса ${params.имя_параметра}

Пример задания параметра
```
 parameters {
        string(name: 'Greeting', defaultValue: 'Hello', description: 'How should I greet the world?')
    }
```

##Обработка ошибок
- Declarative  Pipeline
 Declarative  Pipeline поддерживает надежную обработку отказа по умолчанию через его **post** раздел, который позволяет
 объявлять ряд различных условий "post conditions", таких как :
    - always
    - unstable
    - succes
    - failure
    - changed
    
Пример: 
``` 
pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh 'make check'
            }
        }
    }
    post {
        always {
            junit '**/target/*.xml'
        }
        failure {
            mail to: team@example.com, subject: 'The Pipeline failed :('
        }
    }
}
```

- Scripted Pipeline
Обрабатывает ошибки с помощью встроенного в Groovy **try/catch/finally** семантику.


##Использование нескольких agents

[Пример](https://jenkins.io/doc/book/pipeline/jenkinsfile/#using-multiple-agents):
```
pipeline {
    agent none
    stages {
        stage('Build') {
            agent any
            steps {
                checkout scm
                sh 'make'
                stash includes: '**/target/*.jar', name: 'app' 
            }
        }
        stage('Test on Linux') {
            agent { 
                label 'linux'
            }
            steps {
                unstash 'app' 
                sh 'make check'
            }
            post {
                always {
                    junit '**/target/*.xml'
                }
            }
        }
        stage('Test on Windows') {
            agent {
                label 'windows'
            }
            steps {
                unstash 'app'
                bat 'make check' 
            }
            post {
                always {
                    junit '**/target/*.xml'
                }
            }
        }
    }
}
```

## Advanced Scripted Pipeline// 

##Parallel execution

Pipeline имеет встроенную Функциональноть для параллельного выполнения частей Scripted Pipeline
для этого используйте **parallel** шаг 

Пример:
```
stage('Build') {
    /* .. snip .. */
}

stage('Test') {
    parallel linux: {
        node('linux') {
            checkout scm
            try {
                unstash 'app'
                sh 'make check'
            }
            finally {
                junit '**/target/*.xml'
            }
        }
    },
    windows: {
        node('windows') {
            /* .. snip .. */
        }
    }
}
```



