workflow "New workflow" {
  on = "push"
  resolves = ["Install"]
}

action "Clean" {
  uses = "LucaFeger/action-maven-cli@aed8a1fd96b459b9a0be4b42a5863843cc70724e"
  args = "clean"
}

action "Version" {
  uses = "LucaFeger/action-maven-cli@aed8a1fd96b459b9a0be4b42a5863843cc70724e"
  args = "versions:set -DnewVersion=2.1.14.3"
  needs = ["Clean"]
}

action "Compile" {
  uses = "LucaFeger/action-maven-cli@aed8a1fd96b459b9a0be4b42a5863843cc70724e"
  args = "compile"
  needs = ["Version"]
}

action "Package" {
  uses = "LucaFeger/action-maven-cli@aed8a1fd96b459b9a0be4b42a5863843cc70724e"
  args = "package"
  needs = ["Compile"]
}

action "Re-package" {
  uses = "LucaFeger/action-maven-cli@aed8a1fd96b459b9a0be4b42a5863843cc70724e"
  args = "package javadoc:aggregate-jar"
  needs = ["Package"]
}

action "Install" {
  uses = "LucaFeger/action-maven-cli@aed8a1fd96b459b9a0be4b42a5863843cc70724e"
  args = "install"
  needs = ["Re-package"]
}
