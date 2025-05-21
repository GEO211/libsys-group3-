# Script to download dependencies

# Create lib directory if it doesn't exist
New-Item -ItemType Directory -Force -Path lib

# Dependencies to download
$deps = @(
    @{
        name = "mysql-connector-java-8.0.28.jar"
        url  = "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar"
    },
    @{
        name = "jcalendar-1.4.jar"
        url  = "https://repo1.maven.org/maven2/com/toedter/jcalendar/1.4/jcalendar-1.4.jar"
    },
    @{
        name = "poi-5.2.3.jar"
        url  = "https://repo1.maven.org/maven2/org/apache/poi/poi/5.2.3/poi-5.2.3.jar"
    },
    @{
        name = "poi-ooxml-5.2.3.jar"
        url  = "https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml/5.2.3/poi-ooxml-5.2.3.jar"
    },
    @{
        name = "jackson-databind-2.15.2.jar"
        url  = "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar"
    },
    @{
        name = "jackson-core-2.15.2.jar"
        url  = "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar"
    },
    @{
        name = "jackson-annotations-2.15.2.jar"
        url  = "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.15.2/jackson-annotations-2.15.2.jar"
    },
    @{
        name = "commons-collections4-4.4.jar"
        url  = "https://repo1.maven.org/maven2/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar"
    },
    @{
        name = "commons-io-2.11.0.jar"
        url  = "https://repo1.maven.org/maven2/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar"
    }
)

# Add JFreeChart and its dependencies
$deps += @(
    @{
        name = "jfreechart-1.5.4.jar"
        url  = "https://repo1.maven.org/maven2/org/jfree/jfreechart/1.5.4/jfreechart-1.5.4.jar"
    },
    @{
        name = "jcommon-1.0.24.jar"
        url  = "https://repo1.maven.org/maven2/org/jfree/jcommon/1.0.24/jcommon-1.0.24.jar"
    }
)

# Add these dependencies to the $deps array
$deps += @(
    @{
        name = "xmlbeans-5.1.1.jar"
        url  = "https://repo1.maven.org/maven2/org/apache/xmlbeans/xmlbeans/5.1.1/xmlbeans-5.1.1.jar"
    },
    @{
        name = "poi-ooxml-lite-5.2.3.jar"
        url  = "https://repo1.maven.org/maven2/org/apache/poi/poi-ooxml-lite/5.2.3/poi-ooxml-lite-5.2.3.jar"
    },
    @{
        name = "commons-compress-1.21.jar"
        url  = "https://repo1.maven.org/maven2/org/apache/commons/commons-compress/1.21/commons-compress-1.21.jar"
    },
    @{
        name = "commons-io-2.11.0.jar"
        url  = "https://repo1.maven.org/maven2/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar"
    },
    @{
        name = "log4j-api-2.20.0.jar"
        url  = "https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.20.0/log4j-api-2.20.0.jar"
    }
)

foreach ($dep in $deps) {
    $output = "lib/$($dep.name)"
    if (-not (Test-Path $output)) {
        Write-Host "Downloading $($dep.name)..."
        try {
            Invoke-WebRequest -Uri $dep.url -OutFile $output
            Write-Host "Successfully downloaded $($dep.name)"
        }
        catch {
            Write-Host "Failed to download $($dep.name): $_"
        }
    }
    else {
        Write-Host "$($dep.name) already exists"
    }
}

# Download MySQL JDBC driver
$url = "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar"
$output = "lib/mysql-connector-java-8.0.28.jar"

if (-not (Test-Path $output)) {
    Write-Host "Downloading MySQL JDBC driver..."
    Invoke-WebRequest -Uri $url -OutFile $output
}

# Download Font Awesome
$fontAwesomeUrl = "https://use.fontawesome.com/releases/v5.15.4/fontawesome-free-5.15.4-desktop.zip"
$fontAwesomeZip = "lib/fontawesome.zip"
$fontDir = "fonts"

if (-not (Test-Path $fontDir)) {
    New-Item -ItemType Directory -Force -Path $fontDir
    Invoke-WebRequest -Uri $fontAwesomeUrl -OutFile $fontAwesomeZip
    Expand-Archive -Path $fontAwesomeZip -DestinationPath "temp"
    Move-Item "temp/fontawesome-free-5.15.4-desktop/otfs/Font Awesome 5 Free-Solid-900.otf" "$fontDir/fontawesome-webfont.ttf"
    Remove-Item -Recurse -Force "temp"
    Remove-Item $fontAwesomeZip
} 