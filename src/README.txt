Prosale use a java library, which is not available at Maven repositories 
Therefore, we need to satisfy this dependency locally.

Steps:

mvn install:install-file -Dfile=j4fry-components-1.2_07.jar -DgroupId=org.j4fry -DartifactId=j4fry-components -Dversion=1.2_07 -Dpackaging=jar

