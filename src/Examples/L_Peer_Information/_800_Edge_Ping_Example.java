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

package Examples.L_Peer_Information;

import Examples.Z_Tools_And_Others.Tools;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import net.jxta.endpoint.EndpointService;
import net.jxta.endpoint.MessageTransport;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.endpoint.router.EndpointRouter;
import net.jxta.impl.endpoint.router.RouteControl;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

public class _800_Edge_Ping_Example {
    
    public static final String Name = "Example 800";
    public static final int TcpPort = 9756;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
    
    public static void main(String[] args) {
        
        try {
            
            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);
            
            // Creation of network manager
            NetworkManager MyNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());
            
            // Retrieving the network configurator
            NetworkConfigurator MyNetworkConfigurator = MyNetworkManager.getConfigurator();
            
            // Checking for seeds
            MyNetworkConfigurator.clearRendezvousSeeds();
            String TheSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + _810_RendezVous_Pong_Example.TcpPort;
            Tools.CheckForRendezVousSeedAddition(Name, TheSeed, MyNetworkConfigurator);
            MyNetworkManager.setUseDefaultSeeds(true);

            // Setting Configuration
            MyNetworkConfigurator.setTcpPort(TcpPort);
            MyNetworkConfigurator.setTcpEnabled(true);
            MyNetworkConfigurator.setTcpIncoming(true);
            MyNetworkConfigurator.setTcpOutgoing(true);
            Tools.CheckForMulticastUsage(Name, MyNetworkConfigurator);

            // Setting the Peer ID
            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            MyNetworkConfigurator.setPeerID(PID);
            
            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network and try to establish a route to:\n\n"
                    + _810_RendezVous_Pong_Example.Name);
            PeerGroup NetPeerGroup = MyNetworkManager.startNetwork();
            
            // Retrieving the endpoint service and the message transport
            EndpointService MyEndpointService = NetPeerGroup.getEndpointService();
            MessageTransport MyMessageTransport = MyEndpointService.getMessageTransport("jxta");

            RouteControl MyRouteControl = (RouteControl) MyMessageTransport.transportControl(EndpointRouter.GET_ROUTE_CONTROL, null);
            
            // Can we establish a route to _810_RendezVous_Pong_Example?
            if (MyRouteControl.isConnected(_810_RendezVous_Pong_Example.PID)) {
                Tools.PopInformationMessage(Name, "We can establish a route to: " + _810_RendezVous_Pong_Example.Name);
            } else {
                Tools.PopInformationMessage(Name, "We CAN'T establish a route to: " + _810_RendezVous_Pong_Example.Name);
            }

            // Disabling any rendezvous autostart
            NetPeerGroup.getRendezVousService().setAutoStart(false);
            
            Tools.PopInformationMessage(Name, "Trying to connect to: " + _810_RendezVous_Pong_Example.Name + " for maximum 2 minutes");

            if (MyNetworkManager.waitForRendezvousConnection(120000)) {
                Tools.popConnectedRendezvous(NetPeerGroup.getRendezVousService(),Name);
            } else {
                Tools.PopInformationMessage(Name, "Did not connect to a rendezvous");
            }
            
            // Can we establish a route to _810_RendezVous_Pong_Example?
            if (MyRouteControl.isConnected(_810_RendezVous_Pong_Example.PID)) {
                Tools.PopInformationMessage(Name, "We can establish a route to: " + _810_RendezVous_Pong_Example.Name);
            } else {
                Tools.PopInformationMessage(Name, "We CAN'T establish a route to: " + _810_RendezVous_Pong_Example.Name);
            }
            
            // Trying to connect to seeds
            Tools.PopInformationMessage(Name, "Waiting for the stop of " + _810_RendezVous_Pong_Example.Name);
            
            // Can we establish a route to _810_RendezVous_Pong_Example?
            if (MyRouteControl.isConnected(_810_RendezVous_Pong_Example.PID)) {
                Tools.PopInformationMessage(Name, "We can establish a route to: " + _810_RendezVous_Pong_Example.Name);
            } else {
                Tools.PopInformationMessage(Name, "We CAN'T establish a route to: " + _810_RendezVous_Pong_Example.Name);
            }
            
            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            MyNetworkManager.stopNetwork();
            
        } catch (IOException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (PeerGroupException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }

    }

}