Installation
============

Currently this project is built as an RPM package for RHEL7/CentOS7 and later. The RPM will install the binaries to
`/opt/dans.knaw.nl/dd-lob-store-cli` and the configuration files to `/etc/opt/dans.knaw.nl/dd-lob-store-cli`.

Building from source
--------------------

Prerequisites:

* Java 11 or higher
* Maven 3.3.3 or higher
* RPM

Steps:

```bash
git clone https://github.com/DANS-KNAW/dd-lob-store-cli.git
cd dd-lob-store-cli 
mvn clean install
```
