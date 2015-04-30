
Use following configurations to be able to access certain epics networks:


* Environment - SwissFEL - _casf_

```
ch.psi.jcae.ContextFactory.addressList=sf-cagw
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - SwissFEL NS - _casfns_

```
ch.psi.jcae.ContextFactory.addressList=sf-cans-01 sf-cans-02
ch.psi.jcae.ContextFactory.serverPort 5064
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - Hipa - _cahipa_

```
ch.psi.jcae.ContextFactory.addressList=hipa-cagw
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - OBLA - _caobla_

```
ch.psi.jcae.ContextFactory.addressList=trfcb-cagw
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - FIN - _cafin_

```
ch.psi.jcae.ContextFactory.addressList=fin-ccagw10w
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - Proscan - _capro_

```
ch.psi.jcae.ContextFactory.addressList=proscan-cagw01
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - SLS Machine - _cam_

```
ch.psi.jcae.ContextFactory.addressList=sls-cagw
ch.psi.jcae.ContextFactory.serverPort 5062
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - Office - _cao_

```
ch.psi.jcae.ContextFactory.addressList=129.129.130.255 129.129.131.255 129.129.137.255
ch.psi.jcae.ContextFactory.useShellVariables=false
```

* Environment - SwissFEL SN - _casfsn_

```
ch.psi.jcae.ContextFactory.addressList=172.26.0.255 172.26.8.255 172.26.16.255 172.26.24.255 172.26.32.255 172.26.40.255 172.26.120.255
ch.psi.jcae.ContextFactory.serverPort 5064
ch.psi.jcae.ContextFactory.useShellVariables=false
```