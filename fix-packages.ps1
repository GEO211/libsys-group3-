$files = Get-ChildItem -Path src -Recurse -Filter *.java

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    if ($content -match "package main\.java\.com\.library") {
        $newContent = $content -replace "package main\.java\.com\.library", "package com.library"
        Set-Content -Path $file.FullName -Value $newContent
        Write-Host "Fixed package declaration in $($file.Name)"
    }
} 