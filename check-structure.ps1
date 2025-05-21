# Check and create required directories
$dirs = @(
    "src/com/library/components",
    "src/com/library/dao",
    "src/com/library/main",
    "src/com/library/model",
    "src/com/library/models",
    "src/com/library/panels",
    "src/com/library/sql",
    "src/com/library/ui/auth",
    "src/com/library/util"
)

foreach ($dir in $dirs) {
    if (-not (Test-Path $dir)) {
        Write-Host "Creating directory: $dir"
        New-Item -ItemType Directory -Force -Path $dir
    }
}

# Verify critical files exist
$files = @(
    "src/com/library/main/LoginFrame.java",
    "src/com/library/main/MainFrame.java",
    "src/com/library/main/RegisterFrame.java",
    "src/com/library/util/DatabaseConnection.java",
    "src/com/library/util/SecurityUtil.java",
    "src/com/library/util/ConfigurationUtil.java",
    "src/com/library/dao/BaseDAO.java",
    "src/com/library/dao/BookDAO.java",
    "src/com/library/dao/StudentDAO.java"
)

foreach ($file in $files) {
    if (-not (Test-Path $file)) {
        Write-Host "WARNING: Missing file: $file"
    }
} 