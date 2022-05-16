package io.micronaut.starter.feature.oracecloud

import io.micronaut.starter.ApplicationContextSpec
import io.micronaut.starter.BuildBuilder
import io.micronaut.starter.feature.function.CloudProvider
import io.micronaut.starter.feature.oraclecloud.OracleCloudVault
import io.micronaut.starter.fixture.CommandOutputFixture
import io.micronaut.starter.options.BuildTool
import io.micronaut.starter.options.Language
import spock.lang.Shared
import spock.lang.Subject
import spock.lang.Unroll

class OracleCloudVaultSpec extends ApplicationContextSpec implements CommandOutputFixture {
    @Shared
    @Subject
    OracleCloudVault oracleCloudVault = beanContext.getBean(OracleCloudVault)

    @Unroll
    void "oracle-cloud-vault belongs to cloud ORACLE"() {
        expect:
        oracleCloudVault.cloudProvider.isPresent()
        CloudProvider.ORACLE == oracleCloudVault.cloudProvider.get()
    }

    void 'test readme.md with feature oracle-cloud-vault contains links to micronaut docs'() {
        when:
        def output = generate(['oracle-cloud-vault'])
        def readme = output["README.md"]

        then:
        readme
        readme.contains("https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#vault")
        readme.contains("https://docs.oracle.com/en-us/iaas/Content/KeyManagement/home.htm")
    }

    void 'test src/main/resources/boostrap.yml with feature oracle-cloud-vault contains config'() {
        when:
        def output = generate(['oracle-cloud-vault'])
        def bootstrap = output["src/main/resources/bootstrap.yml"]

        then:
        bootstrap
        bootstrap.contains('''\
micronaut:
  application:
    name: foo
  config-client:
    enabled: true
''')
        bootstrap.contains('''\
oci:
  vault:
    config:
      enabled: true
      vaults:
        compartment-ocid: ''
        ocid: ''
''')
    }

    @Unroll
    void 'test gradle oracle-cloud-vault feature for language=#language'() {
        when:
        String template = new BuildBuilder(beanContext, BuildTool.GRADLE)
                .language(language)
                .features(['oracle-cloud-vault'])
                .render()

        then:
        template.contains('implementation("io.micronaut.oraclecloud:micronaut-oraclecloud-vault")')

        where:
        language << Language.values().toList()
    }

    @Unroll
    void 'test maven oracle-cloud-vault feature for language=#language'() {
        when:
        String template = new BuildBuilder(beanContext, BuildTool.MAVEN)
                .language(language)
                .features(['oracle-cloud-vault'])
                .render()

        then:
        template.contains('''
    <dependency>
      <groupId>io.micronaut.oraclecloud</groupId>
      <artifactId>micronaut-oraclecloud-vault</artifactId>
      <scope>compile</scope>
    </dependency>
''')

        where:
        language << Language.values().toList()
    }
}
