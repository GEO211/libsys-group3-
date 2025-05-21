# Create output directory
New-Item -ItemType Directory -Force -Path target/classes | Out-Null

# Get all Java files
$javaFiles = Get-ChildItem -Path src/com/library -Recurse -Filter *.java | 
    Where-Object { $_.FullName -notlike "*test*" }

# Create file list
$fileList = $javaFiles.FullName -join " "

# Compile
$cmd = "javac -encoding UTF-8 -cp `"lib/*`" -d target/classes $fileList"
Write-Host "Compiling..."
Invoke-Expression $cmd

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful"
    Write-Host "Running application..."
    java -cp "lib/*;target/classes" com.library.main.LoginFrame
} else {
    Write-Host "Compilation failed"
} 