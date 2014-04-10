/*
 * Copyright (c) 2008 DawningStreams, Inc.  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without 
 *  modification, are permitted provided that the following conditions are met:
 *  
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  
 *  2. Redistributions in binary form must reproduce the above copyright notice, 
 *     this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 *  
 *  3. The end-user documentation included with the redistribution, if any, must 
 *     include the following acknowledgment: "This product includes software 
 *     developed by DawningStreams, Inc." 
 *     Alternately, this acknowledgment may appear in the software itself, if 
 *     and wherever such third-party acknowledgments normally appear.
 *  
 *  4. The name "DawningStreams,Inc." must not be used to endorse or promote
 *     products derived from this software without prior written permission.
 *     For written permission, please contact DawningStreams,Inc. at 
 *     http://www.dawningstreams.com.
 *  
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *  DAWNINGSTREAMS, INC OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 *  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *  DawningStreams is a registered trademark of DawningStreams, Inc. in the United 
 *  States and other countries.
 *  
 */

package Examples.G_Simple_Pipe_Communication;

import Examples.Z_Tools_And_Others.Tools;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import net.jxta.credential.AuthenticationCredential;
import net.jxta.credential.Credential;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ProtocolNotSupportedException;
import net.jxta.id.IDFactory;
import net.jxta.impl.membership.pse.FileKeyStoreManager;
import net.jxta.impl.membership.pse.PSEConfig;
import net.jxta.impl.membership.pse.PSEMembershipService;
import net.jxta.impl.membership.pse.PSEUtils;
import net.jxta.impl.membership.pse.StringAuthenticator;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.OutputPipeEvent;
import net.jxta.pipe.OutputPipeListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

public class Edge_Dimitri_Sending_Messages implements OutputPipeListener {
    
    public static final String Name = "Edge Dimitri, Sending Messages";
    public static final int TcpPort = 9723;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
    
    public static final String MyPrincipalName = "Principal - " + Name;
    public static final String MyPrivateKeyPassword = "Private Key Password - " + Name;

    public static final String MyKeyStoreFileName = "MyKeyStoreFile";
    public static final String MyKeyStoreLocation = "." + System.getProperty("file.separator") + Name + File.separator + "MyKeyStoreLocation";
    public static final String MyKeyStorePassword = "KeyStore Password - " + Name;
    public static final String MyKeyStoreProvider = "KeyStore Provider - " + Name;

    public static final File MyKeyStoreDirectory = new File(MyKeyStoreLocation);
    public static final File MyKeyStoreFile = new File(MyKeyStoreLocation + File.separator + MyKeyStoreFileName);

    public static final String Base64X509Certificate = 
        "MIICODCCAaGgAwIBAgIBATANBgkqhkiG9w0BAQUFADBiMRUwEwYDVQQKDAx3d3cuanh0YS5vcmcx" +
        "KjAoBgNVBAMMIUVkZ2UgRGltaXRyaSwgU2VuZGluZyBNZXNzYWdlcy1DQTEdMBsGA1UECwwUN0Ix" +
        "NUJGMENGQzkwQTQwRUY0OEYwHhcNMDgwNjAyMjMyODQwWhcNMTgwNjAyMjMyODQwWjBiMRUwEwYD" +
        "VQQKDAx3d3cuanh0YS5vcmcxKjAoBgNVBAMMIUVkZ2UgRGltaXRyaSwgU2VuZGluZyBNZXNzYWdl" +
        "cy1DQTEdMBsGA1UECwwUN0IxNUJGMENGQzkwQTQwRUY0OEYwgZ8wDQYJKoZIhvcNAQEBBQADgY0A" +
        "MIGJAoGBAIzftcj9j6JGvwzAlatHzaD1QTw1kesFaD/KS+l4ja8gZcfzMl//S7xsOpWw1TRK4bi+" +
        "1u659jD+mee1nCFSkUnNd4V5JYt9nSc6p1Seo6prVD3beBVAlDRqkxubVIuJALMN1w6Jv4+y9hTs" +
        "fPmGtYWcDK+M41CKQ0yxzblFGGdjAgMBAAEwDQYJKoZIhvcNAQEFBQADgYEAGfBXMXOKvUh0eba6" +
        "8/y9RMP2obXMFKKpyWiC1BT4CXYw6gai3CocIzUyUkZKQQQU41sm//tq2WhBa8eY/0YdQYPDll1C" +
        "Ti4Q/Jj64mpSeyGJmrNoSpj6OetkY9RW1IvYEZJ/MNU+XGN+XI/kSEebiruhblRox3qhykSPqUhW" +
        "Cs0=";
        
    public static final String Base64PrivateKey = 
        "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIzftcj9j6JGvwzAlatHzaD1QTw1" +
        "kesFaD/KS+l4ja8gZcfzMl//S7xsOpWw1TRK4bi+1u659jD+mee1nCFSkUnNd4V5JYt9nSc6p1Se" +
        "o6prVD3beBVAlDRqkxubVIuJALMN1w6Jv4+y9hTsfPmGtYWcDK+M41CKQ0yxzblFGGdjAgMBAAEC" +
        "gYBin2dLm8BHslGkIttlOvKJYcnW1gYey/1M01pLy/sdBIhc8DRvInKqKfblvxOz9UyLIwejv4rM" +
        "iCHFJs50QbK2NeIT0ocX5gkkceKnxTTQO6Fk3+sjS12gHR2aLFfCsuFbLK2sAZoeTZ353fmUTTPE" +
        "bcYG5tHyo5NClg/BMcApKQJBANPxCriw8g3A7IYhVbA6OV1E2MglqAog4GYHUXXiqVT8CLjjyyoc" +
        "jdt5VPv2wXp+8OSn1d3W1MDX9czFa9H32U8CQQCqKKMRJK38cHpc/q+EUpzhb6hhjeBpt39pgu1y" +
        "88Fec3/PImKVqfAJ3rhc1u7BLuk0vm2IxIJRUV7u4lqZH2OtAkBMVWB7LJ/BfF6z4IWwNwg6I3XA" +
        "gG+JVlX76V7Zio1aYnvxu3mM7UPabFTDexgsOlQgxqIsiBeQbTfGTMSMlyvZAkEAl/6WsuCN3dnn" +
        "I5yI0e0tn54qzYF7PVOhJ3HBgxZD24fWWgNABqp29YoSKzP+r2ek/u0/UJUgR10lLMPa476LPQJA" +
        "BAMqdH5iTykEpya9PZPFupfMuJD7797jblMmN7Jkar801pvWDQmJJr6V88xeDmTOKUoLcVGETjvO" +
        "6KeSL0DtXQ==";

    // Catching connections to the pipe
    public void outputPipeEvent(OutputPipeEvent OPE) {
        
        try {

            // Notifying the user
            Tools.PopInformationMessage(Name, "Someone connected to the pipe, sending hello message!");

            // Retrieving the output pipe to the peer who connected to the input end
            OutputPipe MyOutputPipe = OPE.getOutputPipe();
            
            // Creating a Hello message !!!
            Message MyMessage = new Message();
            StringMessageElement MyStringMessageElement = new StringMessageElement("HelloElement", "Hello from " + Name, null);
            MyMessage.addMessageElement("DummyNameSpace", MyStringMessageElement);

            // Sending the message
            MyOutputPipe.send(MyMessage);
            
        } catch (IOException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }
        
    }    
    
    public static void main(String[] args) {
        
        try {
            
            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);
            
            // Recreating the X509 certificate
            X509Certificate TheX509Certificate = null;
            X509Certificate OtherPeerX509Certificate = null;
            PrivateKey ThePrivateKey = null;
    
            CertificateFactory TheCF = CertificateFactory.getInstance("X509");
            
            byte[] Temp = PSEUtils.base64Decode(new StringReader(Base64X509Certificate));
            TheX509Certificate = (X509Certificate) TheCF.generateCertificate(new ByteArrayInputStream(Temp));

            Temp = PSEUtils.base64Decode(new StringReader(RendezVous_Chandra_Receiving_Messages.Base64X509Certificate));
            OtherPeerX509Certificate = (X509Certificate) TheCF.generateCertificate(new ByteArrayInputStream(Temp));

            // Restoring the private key
            Temp = PSEUtils.base64Decode(new StringReader(Base64PrivateKey));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec MyPKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(Temp);
            ThePrivateKey = keyFactory.generatePrivate(MyPKCS8EncodedKeySpec);

            // Preparing data
            MyKeyStoreDirectory.mkdirs();
            
            // Creating the key store
            FileKeyStoreManager MyFileKeyStoreManager = new FileKeyStoreManager(
                (String)null, MyKeyStoreProvider, MyKeyStoreFile);
            
            MyFileKeyStoreManager.createKeyStore(MyKeyStorePassword.toCharArray());
            
            // Loading the (empty) keystore 
            KeyStore MyKeyStore = MyFileKeyStoreManager.loadKeyStore(MyKeyStorePassword.toCharArray());
            
            // Adding certificates and public keys
            X509Certificate[] TempX509 = { TheX509Certificate };
            MyKeyStore.setKeyEntry(PID.toString(), ThePrivateKey, MyPrivateKeyPassword.toCharArray(), TempX509);

            MyKeyStore.setCertificateEntry(RendezVous_Chandra_Receiving_Messages.PID.toString(), OtherPeerX509Certificate);
            
            // Saving the data
            MyFileKeyStoreManager.saveKeyStore(MyKeyStore, MyKeyStorePassword.toCharArray());
            
            // Creation of network manager
            NetworkManager MyNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());
            
            // Retrieving the network configurator
            NetworkConfigurator MyNetworkConfigurator = MyNetworkManager.getConfigurator();
            
            // Setting the key store location
            MyNetworkConfigurator.setKeyStoreLocation(MyKeyStoreFile.toURI());
            
            // Checking if RendezVous_Jack should be a seed
            MyNetworkConfigurator.clearRendezvousSeeds();
            String TheSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVous_Chandra_Receiving_Messages.TcpPort;
            Tools.CheckForRendezVousSeedAddition(Name, TheSeed, MyNetworkConfigurator);

            // Setting Configuration
            MyNetworkConfigurator.setTcpPort(TcpPort);
            MyNetworkConfigurator.setTcpEnabled(true);
            MyNetworkConfigurator.setTcpIncoming(true);
            MyNetworkConfigurator.setTcpOutgoing(true);
            Tools.CheckForMulticastUsage(Name, MyNetworkConfigurator);

            // Setting the Peer ID
            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            MyNetworkConfigurator.setPeerID(PID);
            
            // Saving the configuration
            MyNetworkConfigurator.save();

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network and to wait for a rendezvous connection with\n"
                    + RendezVous_Chandra_Receiving_Messages.Name + " for maximum 2 minutes");
            PeerGroup NetPeerGroup = MyNetworkManager.startNetwork();
            
            // Disabling any rendezvous autostart
            NetPeerGroup.getRendezVousService().setAutoStart(false);
            
            if (MyNetworkManager.waitForRendezvousConnection(120000)) {
                Tools.popConnectedRendezvous(NetPeerGroup.getRendezVousService(),Name);
            } else {
                Tools.PopInformationMessage(Name, "Did not connect to a rendezvous");
            }
            
            // Retrieving the PSE membership service
            PSEMembershipService MyMembershipService = (PSEMembershipService) NetPeerGroup.getMembershipService();

            // Joining the peer group
            AuthenticationCredential MyAuthenticationCredit = new
                AuthenticationCredential( NetPeerGroup, "StringAuthentication", null );

            StringAuthenticator MyStringAuthenticator = (StringAuthenticator) MyMembershipService.apply(MyAuthenticationCredit);

            MyStringAuthenticator.setAuth1_KeyStorePassword(MyKeyStorePassword);
            MyStringAuthenticator.setAuth2Identity(PID);
            MyStringAuthenticator.setAuth3_IdentityPassword(MyPrivateKeyPassword);

            Credential MyCredential = null;

            if (MyStringAuthenticator.isReadyForJoin()) {
                MyCredential = MyMembershipService.join(MyStringAuthenticator);
            }
            
            if (MyCredential!=null) {
                Tools.PopInformationMessage(Name, "Credentials created successfully");
            } else {
                Tools.PopInformationMessage(Name, "Credentials NOT created successfully");
            }
            
            // Check key store content
            PSEConfig MyPSEConfig = MyMembershipService.getPSEConfig();
            
            X509Certificate MyX509Check = MyPSEConfig.getTrustedCertificate(PID);
            
            if (MyX509Check==null) {
                Tools.PopInformationMessage(Name, "X509 not found!");
            } else {
                Tools.PopInformationMessage(Name, "X509 found!");
            }
            
            X509Certificate OtherPeerX509Check = MyPSEConfig.getTrustedCertificate(RendezVous_Chandra_Receiving_Messages.PID);
            
            if (OtherPeerX509Check==null) {
                Tools.PopInformationMessage(Name, "X509 for other peer not found!");
            } else {
                Tools.PopInformationMessage(Name, "X509 for other peer found!");
            }

            PrivateKey MyPrivateKeyCheck = MyPSEConfig.getKey(PID, MyPrivateKeyPassword.toCharArray());
            
            if (MyPrivateKeyCheck==null) {
                Tools.PopInformationMessage(Name, "Private key not found!");
            } else {
                Tools.PopInformationMessage(Name, "Private key found!");
            }

            // Creating an ouput pipe
            PipeService MyPipeService = NetPeerGroup.getPipeService();
            MyPipeService.createOutputPipe(RendezVous_Chandra_Receiving_Messages.GetPipeAdvertisement(),
                    new Edge_Dimitri_Sending_Messages());
            
            // Displaying pipe advertisement to identify potential compilation issues
            System.out.println(RendezVous_Chandra_Receiving_Messages.GetPipeAdvertisement().toString());

            // Going to sleep for some time
            Tools.GoToSleep(60000);
            
            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            MyNetworkManager.stopNetwork();
            
        } catch (ProtocolNotSupportedException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (InvalidKeySpecException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (NoSuchAlgorithmException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (CertificateException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (NoSuchProviderException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (KeyStoreException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (IOException Ex) {
            
            // Raised when access to local file and directories caused an error
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (PeerGroupException Ex) {
            
            // Raised when the net peer group could not be created
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }

    }

}