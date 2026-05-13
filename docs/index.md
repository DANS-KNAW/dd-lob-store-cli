dd-lob-store-cli
===========

<!-- Remove this comment and extend the descriptions below -->


SYNOPSIS
--------

    dd-lob-store-cli { server | check }


DESCRIPTION
-----------

CLI for dd-lob-store


ARGUMENTS
---------

        positional arguments:
        {server,check}         available commands
        
        named arguments:
        -h, --help             show this help message and exit
        -v, --version          show the application version and exit

EXAMPLES
--------

<!-- Add examples of invoking this module from the command line or via HTTP other interfaces -->
    

INSTALLATION AND CONFIGURATION
------------------------------
Currently this project is built as an RPM package for RHEL7/CentOS7 and later. The RPM will install the binaries to
`/opt/dans.knaw.nl/dd-lob-store-cli` and the configuration files to `/etc/opt/dans.knaw.nl/dd-lob-store-cli`. 

BUILDING FROM SOURCE
--------------------
Prerequisites:

* Java 11 or higher
* Maven 3.3.3 or higher
* RPM

Steps:
    
    git clone https://github.com/DANS-KNAW/dd-lob-store-cli.git
    cd dd-lob-store-cli 
    mvn clean install
