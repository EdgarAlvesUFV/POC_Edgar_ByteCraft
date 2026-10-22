$SONAR_HOST_URL="http://localhost:9000"
$SONAR_TOKEN="squ_53ff9f4400026bfb98e6e50e241efdde048e910e"

$ignore = @(
    ".git",
    ".github",
    ".vscode",
    "node_modules",
    ".scannerwork",
    "dist",
    "build",
    "target"
)

Get-ChildItem -Directory | Where-Object {
    $ignore -notcontains $_.Name
} | ForEach-Object {

    Write-Host "==============================="
    Write-Host "Scanning $($_.Name)"
    Write-Host "==============================="

    Set-Location $_.FullName

    sonar-scanner `
      "-Dsonar.projectKey=$($_.Name)" `
      "-Dsonar.projectName=$($_.Name)" `
      "-Dsonar.sources=." `
      "-Dsonar.exclusions=**/*.java" `
      "-Dsonar.host.url=$SONAR_HOST_URL" `
      "-Dsonar.login=$SONAR_TOKEN" `
      "-Dsonar.sourceEncoding=UTF-8"

    Set-Location ..
}