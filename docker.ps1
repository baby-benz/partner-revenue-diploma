docker login

$repositoryName = "babybenz/partner-reward"

$versions = @(
    "0.0.2-SNAPSHOT",
    "0.0.2-SNAPSHOT",
    "0.0.3-SNAPSHOT",
    "0.0.2-SNAPSHOT",
    "0.0.2-SNAPSHOT"
)

$images = @(
    "calc-scheme-service",
    "profile-point-service",
    "event-processor",
    "reward-calculator",
    "report"
)

$versionedImages = foreach ($i in 0..($images.count - 1)) { 
    "$($images[$i]):$($versions[$i])" 
}

$tags = foreach ($i in 0..($images.count - 1)) { 
    "$($repositoryName):$($images[$i])-$($versions[$i])" 
}

for ($i=0; $i -lt $versionedImages.Length; $i++) {
    docker tag $versionedImages[$i] $tags[$i]

    docker push $tags[$i]
}