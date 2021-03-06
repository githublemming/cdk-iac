/*
 * MIT License
 *
 * Copyright (c) 2018 Mark Haskins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.MIT License
 */

package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import io.haskins.cdkiac.utils.AppProps;
import io.haskins.cdkiac.stack.CdkIacStack;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.rds.CfnDBInstance;
import software.amazon.awscdk.services.rds.CfnDBInstanceProps;

import java.util.Collections;


public class RDS extends CdkIacStack {

    public RDS(final App parent,
               final String name,
               final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private RDS(final App parent,
                final String name,
                final StackProps props,
                final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {

        try {

            IVpcNetwork vpc = VpcNetwork.import_(this,"Vpc", VpcNetworkImportProps.builder()
                    .withVpcId(appProps.getPropAsString("vpcId"))
                    .withAvailabilityZones(appProps.getPropAsStringList("availabilityZones"))
                    .withPublicSubnetIds(appProps.getPropAsStringList("elbSubnets"))
                    .withPrivateSubnetIds(appProps.getPropAsStringList("ec2Subnets"))
                    .build());

            SecurityGroup sg = new SecurityGroup(this,"RdsSecurityGroup", SecurityGroupProps.builder()
                    .withAllowAllOutbound(true)
                    .withDescription(uniqueId)
                    .withGroupName(uniqueId)
                    .withVpc(vpc)
                    .build());

            sg.addIngressRule(new CidrIPv4(appProps.getPropAsString("my_cidr")), new TcpPort(3306));
            sg.addIngressRule(new CidrIPv4(appProps.getPropAsString("vpc_cidr")), new TcpPort(3306));

            new CfnDBInstance(this, "Rds", CfnDBInstanceProps.builder()
                    .withAllocatedStorage(appProps.getPropAsString("rds_storage"))
                    .withStorageType("gp2")
                    .withDbInstanceClass(appProps.getPropAsString("rds_ec2"))
                    .withDbInstanceIdentifier(uniqueId)
                    .withDbSubnetGroupName(appProps.getPropAsString("rds_subnet"))
                    .withEngine(appProps.getPropAsString("rds_engine"))
                    .withEngineVersion(appProps.getPropAsString("rds_version"))
                    .withMasterUsername("Root")
                    .withMasterUserPassword("0000") // they'll never guess that :)
                    .withMultiAz(appProps.getPropAsBoolean("rds_multi-az"))
                    .withVpcSecurityGroups(Collections.singletonList(sg.getSecurityGroupId()))
                    .withDbParameterGroupName(appProps.getPropAsString("rds_param_group"))
                    .build());

        } catch (MissingPropertyException e) {
            throw new StackException(e.getMessage());
        }

    }
}
