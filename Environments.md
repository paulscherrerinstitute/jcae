
Use following configurations to be able to access certain epics networks:


* Environment - _casf_ - SwissFEL

```
ch.psi.jcae.ContextFactory.addressList=sf-cagw
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - _casfns_ - SwissFEL NS

```
ch.psi.jcae.ContextFactory.addressList=sf-cans-01 sf-cans-02
ch.psi.jcae.ContextFactory.serverPort 5064
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - _cahipa_ - Hipa

```
ch.psi.jcae.ContextFactory.addressList=hipa-cagw
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - _caobla_ - OBLA

```
ch.psi.jcae.ContextFactory.addressList=trfcb-cagw
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - _cafin_ - FIN

```
ch.psi.jcae.ContextFactory.addressList=fin-ccagw10w
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - _capro_ - Proscan

```
ch.psi.jcae.ContextFactory.addressList=proscan-cagw01
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - _cam_ - SLS Machine

```
ch.psi.jcae.ContextFactory.addressList=sls-cagw
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - _cao_ - Office

```
ch.psi.jcae.ContextFactory.addressList=129.129.130.255 129.129.131.255 129.129.137.255
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - _casfsn_ - SwissFEL SN

```
ch.psi.jcae.ContextFactory.addressList=172.26.0.255 172.26.8.255 172.26.16.255 172.26.24.255 172.26.32.255 172.26.40.255 172.26.120.255
ch.psi.jcae.ContextFactory.serverPort 5064
ch.psi.jcae.ContextFactory.useShellVariables=false
```