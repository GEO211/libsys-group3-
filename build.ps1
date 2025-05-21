# Build script for Library Management System

# Create build directories
New-Item -ItemType Directory -Force -Path target/classes
New-Item -ItemType Directory -Force -Path target/test-classes

# Set classpath with all dependencies
$deps = @(
    "lib/mysql-connector-java-8.0.28.jar",
    "lib/HikariCP-5.0.1.jar",
    "lib/javax.mail-1.6.2.jar",
    "lib/poi-5.2.3.jar",
    "lib/poi-ooxml-5.2.3.jar",
    "lib/log4j-api-2.20.0.jar",
    "lib/log4j-core-2.20.0.jar",
    "lib/jackson-databind-2.15.2.jar",
    "lib/jbcrypt-0.4.jar",
    "lib/hamcrest-core-2.2.jar",
    "lib/slf4j-api-2.0.7.jar",
    "lib/slf4j-simple-2.0.7.jar"
)

# Create lib directory if it doesn't exist
New-Item -ItemType Directory -Force -Path lib

# Download dependencies if they don't exist
foreach ($dep in $deps) {
    if (-not (Test-Path $dep)) {
        Write-Host "Dependency $dep not found. Please download it manually and place it in the lib directory."
    }
}

# Set source and test directories
$srcDir = "src/main/java"
$testDir = "src/test/java"
$targetDir = "target/classes"
$testTargetDir = "target/test-classes"

# Compile source files
Write-Host "Compiling source files..."
$sourceFiles = Get-ChildItem -Path $srcDir -Filter *.java -Recurse
javac -d $targetDir -cp "$($deps -join ';')" $sourceFiles.FullName

# Compile test files
Write-Host "Compiling test files..."
$testFiles = Get-ChildItem -Path $testDir -Filter *.java -Recurse
javac -d $testTargetDir -cp "$targetDir;$($deps -join ';');lib/junit-4.13.2.jar;lib/mockito-core-5.3.1.jar" $testFiles.FullName

# Run tests
Write-Host "Running tests..."
java -cp "$testTargetDir;$targetDir;$($deps -join ';');lib/junit-4.13.2.jar;lib/mockito-core-5.3.1.jar" org.junit.runner.JUnitCore test.java.com.library.dao.BaseDAOTest

# Run main application
Write-Host "Running application..."
java -cp "$targetDir;$($deps -join ';')" main.java.com.library.main.Main 