$javaRoot = Join-Path $PSScriptRoot '..\src\main\java' -Resolve
$resItems = Join-Path $PSScriptRoot '..\src\main\resources\assets\hbm\textures\items' -Resolve

$refs = New-Object System.Collections.Generic.HashSet[string]
Get-ChildItem -Path $javaRoot -Recurse -Filter *.java | ForEach-Object {
    $text = Get-Content -Raw -Path $_.FullName -ErrorAction SilentlyContinue
    if ($null -ne $text) {
        # match RefStrings.MODID + ":name"
        [regex]::Matches($text, 'RefStrings\.MODID\s*\+\s*\"?:([a-zA-Z0-9_\./-]+)\"?') | ForEach-Object { $refs.Add($_.Groups[1].Value) | Out-Null }
        # match setTextureName("modid:name")
        [regex]::Matches($text, 'setTextureName\s*\(\s*\"[a-zA-Z0-9_]+:([a-zA-Z0-9_\./-]+)\"\s*\)') | ForEach-Object { $refs.Add($_.Groups[1].Value) | Out-Null }
        # match setTextureName(RefStrings.MODID + ":name") variant already covered
        # fallback: naive colon match inside setTextureName
        [regex]::Matches($text, 'setTextureName\s*\([^\)]*:\s*([a-zA-Z0-9_\./-]+)') | ForEach-Object { $refs.Add($_.Groups[1].Value) | Out-Null }
    }
}

# gather files
$files = Get-ChildItem -Path $resItems -File -Filter *.png | ForEach-Object { $_.BaseName }

$missing = $refs | Where-Object { $_ -and ($_ -notin $files) -and ($_ -notlike '*/ *') }
$missing = $missing | Sort-Object -Unique
Write-Output "Found $($refs.Count) texture refs and $($files.Count) item png files."
Write-Output "Missing texture names (count=$($missing.Count)):"
$missing | ForEach-Object { Write-Output $_ }
$missing | Out-File -FilePath (Join-Path $PSScriptRoot 'missing_textures.txt') -Encoding utf8
Write-Output "Wrote missing_textures.txt"