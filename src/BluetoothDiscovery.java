

import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import java.io.*;
import java.util.Vector;
import javax.bluetooth.*;
import java.util.Timer;
import java.util.TimerTask;



public class BluetoothDiscovery extends Alert implements CommandListener
{
  
    static public final boolean CONCEPT_SDK_BUILD = false;   
  
    static private final int SERVICE_NAME_BASE_LANGUAGE = 0x0100;
    // The major device class (in CoD) used for phones:
    static private final int MAJOR_DEVICE_CLASS_PHONE = 0x0200;

    static public final int SEARCH_CONNECT_FIRST_FOUND = 1;
   
    static public final int SEARCH_CONNECT_ALL_FOUND = 2;
    
    static public final int SEARCH_ALL_DEVICES_SELECT_ONE = 3;
  
    static public final int SEARCH_ALL_DEVICES_SELECT_SEVERAL = 4;
 
    static private final int BLUETOOTH_MAX_DEVICES = 7;

    private Display display;  // The used display

    // Bluetooth/JSR82 variables
    private LocalDevice localDevice = null;
    private int previousDiscoverabilityMode = -1; 
    
    private String serviceUUID = null;

    private String localName = null;

    // Used for Client
    private DiscoveryAgent discoveryAgent; 
    private Listener listener; 
    private int searchType;   
    private int serviceSearchTransId;
    private Vector urlStrings; 
    private Vector foundServiceRecords; 
    private int maxDevices; 
    private DeviceList deviceList;  
    private String warning;

    // Used for Server
    private StreamConnectionNotifier notifier;  // For the server
    // Used for both
    private BluetoothConnection[] btConnections;  // All Bluetooth connections

    private BluetoothDiscovery  root;

    
    private InqProgressBar progressBar;

   
    private Object block_c;	// for Client
    private Object block_s;	// for Server
    private Object block_ss;	// for termination of service search
    private Object block_notifier;   // For Notifier.close

   
    public BluetoothDiscovery( Display disp )
    {
        super( "" );
        // store 'this'
        root = this;
        // store display
        display = disp;
        // Initialize
        progressBar = null;
        deviceList = null;
        
        block_c = new Object();
        block_s = new Object();
        block_ss = new Object();
        block_notifier = new Object();

        try
        {  
            localDevice = LocalDevice.getLocalDevice();
          
            maxDevices = Integer.parseInt( localDevice.getProperty( "bluetooth.connected.devices.max" ) );
           
            if( maxDevices > BLUETOOTH_MAX_DEVICES )
            {   // limit to 7
                maxDevices = BLUETOOTH_MAX_DEVICES;
            }
        }
        catch(Exception e)
        {   
            localDevice = null;

            String message = "Error trying to get local device properties: " +
                             e.getMessage();
            ErrorScreen.showError(message, display.getCurrent());
        }

    }   
    private void closeNotifier()
    {
        synchronized( block_notifier )
        {
            if( notifier != null )
            {
                try
                {
                    if( ! CONCEPT_SDK_BUILD )
                    {   
                        notifier.close();
                    }
                }
                catch(Exception e)
                {
                    String message = "Error trying to close notifier" +
                                     e.getMessage();
                    ErrorScreen.showError(message, display.getCurrent());
                }
                notifier = null;
            }
        }
    }

    /**
     * Respond to commands.
     * @param c The command.
     * @param s The displayable object. */
    public void commandAction(Command c, Displayable s)
    {
        switch( c.getCommandType() )
        {
            case Command.CANCEL:
                
                try
                {
                    
                    if( listener != null )
                    { 
                        Alert a = new Alert( "", "Search cancelled", null, AlertType.INFO );
                        a.setTimeout( 10000 );  // display max. 10 secs
                        display.setCurrent( a );
                       
                        discoveryAgent.cancelInquiry( listener );
                       
                        waitOnServSearchTermination w = new waitOnServSearchTermination();
                        w.start();
                    }
                    
                    listener = null;
                    
                    closeNotifier();
                   
                    if( progressBar != null )
                    {
                        progressBar.stop();
                        progressBar = null;
                    }
                }
                catch(Exception e)
                {
                    String message = "Error trying to cancel: " +
                                     e.getMessage();
                    ErrorScreen.showError(message, display.getCurrent());
                }
                break;
        }
    }

    
    private void saveDiscoverability()
    {
        try
        {
            // Store discoverability mode
            previousDiscoverabilityMode =
                LocalDevice.getLocalDevice().getDiscoverable();
        }
        catch(Exception e)
        {
          
        }
    }

  
    private void restoreDiscoverability()
    {
        try
        {   
            if( previousDiscoverabilityMode != -1 )
            {
                localDevice.setDiscoverable( previousDiscoverabilityMode );
            }
        }
        catch( Exception e )
        {
           
        }
    }


  
    public void setName( String ln )
    {
        
        localName = ln;
    }

   
    public void setServiceUUID( String UUID )
    {
        // store UUID
        serviceUUID = UUID;
       
        if( CONCEPT_SDK_BUILD )
        {   
            Alert a = new Alert( "", "CONCEPT SDK Build", null, AlertType.INFO );
            a.setTimeout( 1000 );
            display.setCurrent( a );

            try
            {
                Thread.sleep( 1000 );
            }
            catch(InterruptedException e )
            {
                // We can just ignore
            }
        }
    }


  
    public BluetoothConnection[] searchService( int st ) throws BluetoothStateException, InterruptedException, IOException
    {
        StreamConnection con;
        DataElement de;
        String rname;

        // Reset search transaction id
        serviceSearchTransId = -1;

        // store search type
        searchType = st;

       
        // Initialize
        foundServiceRecords = new Vector();
        urlStrings = new Vector();

        
        discoveryAgent = localDevice.getDiscoveryAgent();

        
        listener = new Listener();

        // Show progress bar for Inquiry
        progressBar = new InqProgressBar( "Search Devices...", 105 );

        // Init warning string
        warning = "";

        
        synchronized( block_c )
        {   // start the inquiry on LIAC only
            discoveryAgent.startInquiry( DiscoveryAgent.LIAC, listener );
            // wait
            block_c.wait();
        }

        // Release List object
        deviceList = null;

        // Stop progress bar
        if( progressBar != null )
        {
            progressBar.stop();
        }
        
       
        if( ! warning.equals( "" ) )
        {   // Do 2 secs alert
            Alert al = new Alert( null, warning, null, AlertType.INFO );
            // Show 2 seconds
            al.setTimeout( 2000 );
            display.setCurrent( al );
            // wait
            synchronized( al )
            {
                try
                {
                    al.wait( 2000 );
                }
                catch(InterruptedException e )
                {
                    // Shouldn't happen in MIDP
                }
            }
        }

        // Create list
        btConnections = new BluetoothConnection[urlStrings.size()];
        // Check if devices have been found
        if( urlStrings.size() > 0 )
        {   // connect only if devices have been found
            // Start connection progress bar
            progressBar = new PageProgressBar( "Connecting...", urlStrings.size() );
            // Connect all devices
            for( int i=0; i<urlStrings.size(); i++ )
            {   // Retrieve remote name
                de = ((ServiceRecord)foundServiceRecords.elementAt(i)).getAttributeValue( SERVICE_NAME_BASE_LANGUAGE );
                rname = (String) de.getValue();
                // Update progress bar
                ((PageProgressBar)progressBar).nextDevice();
                btConnections[i] = new BluetoothConnection( (String) urlStrings.elementAt(i), localName, rname );
                // Send name to remote device
                btConnections[i].writeString( localName );
            }
            // Stop (connecting) progress bar
            progressBar.stop();
        }
        // Delete progressBar
        progressBar = null;
        // reset listener
        listener = null;

        return btConnections;
    }


  
    public BluetoothConnection[] waitOnConnection() throws BluetoothStateException, IOException, InterruptedException
    {
        acceptAndOpenThread t;
        String ServiceName;

        // Save Discoverability Mode
        saveDiscoverability();

        // Go in Limited Inquiry scan mode
        localDevice.setDiscoverable( DiscoveryAgent.LIAC );

        // Call connector.open to create Notifier object
        notifier = (StreamConnectionNotifier) Connector.open( "btspp://localhost:" + serviceUUID + ";name=" + localName + ";authorize=false;authenticate=false;encrypt=false" );

        // Show text box with possibility to cancel the server session.
        setTitle( "Waiting" );
        setString( "Waiting for someone to connect..." );
        setTimeout( FOREVER );
        addCommand( new Command( "Cancel", Command.CANCEL, 1 ) );
        setCommandListener( this );
        display.setCurrent( this );

        // Spawn new thread which does acceptandopen
        t = new acceptAndOpenThread();

        // wait on thread (until someone connects)
        synchronized(  block_s )
        {
            // Start acceptAndOpen
            t.start();
            // wait
            block_s.wait();
        }

        
        notifier = null;
       
        restoreDiscoverability();
   
        return btConnections;
    }

    
    private class InqProgressBar
    extends TimerTask
    {
        
        protected Gauge gauge;
       
        protected Timer tm;

        /**
         * Constructor
         * @param ga Gauge object that should be updated.
         */
        private InqProgressBar( String title, int max )
        {
            
            gauge = new Gauge( title, false, max, 0 );
            Command cmStop = new Command( "Cancel", Command.CANCEL, 1 );

            // Create the form, add gauge & stop command, listen for events
            Form f = new Form("");
            f.append( gauge );
            f.addCommand( cmStop );
            f.setCommandListener( root );
            display.setCurrent( f );

            // Start timer that fires off every 100 ms
            tm = new Timer();
            tm.scheduleAtFixedRate( this, 0, 100);
        }

        /**
         * The run method.
         */
        public void run()
        {
            int time;

            // add one second
            time = gauge.getValue() + 1;

            // Is current value of gauge less than the max?
            if( time > gauge.getMaxValue() )
            {   // Begin at 0
                time = 0;
            }

            // Store new value
            gauge.setValue( time );
        }

        /**
         * Stops the timer
         */
        protected void stop()
        {
            // Stop the timer
            cancel();
            tm.cancel();
        }
    }

    // Inner class
    // Updates the page progress bar/gauge based on a timer.
    private class PageProgressBar
    extends InqProgressBar
    {
        static final int PAGE_TIME = 30; // in 1/10 secs, 3 secs
        private int timer_max;
        /**
         * Constructor
         * @param ga Gauge object that should be updated.
         */
        private PageProgressBar( String str, int countDev )
        {
            super( str, countDev*PAGE_TIME );
            // Set first timer_max value
            timer_max = 0;
        }

        /**
         * The run method.
         */
        public final void run()
        {
            int time;

            // add one second
            time = gauge.getValue() + 1;

            // Is current value of gauge less than the max?
            if( time > timer_max )
            {   // Stop
                time = timer_max;
            }

            // Store new value
            gauge.setValue( time );
        }

        /**
         * Start progress bar for next device.
         */
        public void nextDevice()
        {
            // Set current value
            gauge.setValue( timer_max );
            // Stop the timer
            timer_max += PAGE_TIME;
        }
    }


    private class Listener
    implements DiscoveryListener
    {
        private Vector cached_devices;
        ServiceRecord  currServRec;

        /** Constructor
         */
        public Listener()
        {
            // Initialize
            cached_devices = new Vector();
        }

      
        public void deviceDiscovered( RemoteDevice btDevice, DeviceClass cod )
        {
         
            if( ! CONCEPT_SDK_BUILD )
            {   // Concept SDK returns wrong values for CoD
                if( cod.getMajorDeviceClass() != MAJOR_DEVICE_CLASS_PHONE )
                {   // return in case it's not a phone
                    return;
                }
            }

            // It's another phone, so store it in the list
            if( ! cached_devices.contains( btDevice ) )
            {   // But only if it is not already in the list (same device might be reported more than once)
                cached_devices.addElement( btDevice );
            }
        }


  
        public void inquiryCompleted( int discType )
        {
            if( discType == INQUIRY_COMPLETED )
            {   // Check if devices have been found
                if( cached_devices.size() == 0 )
                {   // No device found
                    warning = "No devices found!";
                }
                else
                {   // Stop Inquiry progress bar
                    progressBar.stop();
                    // Start service search progress bar
                    progressBar = new PageProgressBar( "Search Service...", cached_devices.size() );
                    // start service search
                    nextDeviceServiceSearch();
                    return;
                }
            }

            
            synchronized( block_c )
            {
                block_c.notifyAll();
            }
            // Note: progressBar is anyway stopped by searchService method
        }

     
        private void nextDeviceServiceSearch()
        {
            UUID[] u = new UUID[1];
            u[0] = new UUID( serviceUUID, false );
            int attrbs[] =
            { SERVICE_NAME_BASE_LANGUAGE }; // Retrieved service record should include service name
            RemoteDevice dev;

            // Update progress bar
            ((PageProgressBar)progressBar).nextDevice();

            // Retrieve next device
            try
            {
                dev = (RemoteDevice) cached_devices.firstElement();
                cached_devices.removeElementAt( 0 );
            }
            catch( Exception e )
            {   // All devices searched
                if( foundServiceRecords.size() == 0 )
                {   // If no device found then alert to user
                    warning = "No service found!";
                }
                // If service not found on any device return to main,
                // also if SEARCH_CONNECT_ALL_FOUND was selected.
                if( (foundServiceRecords.size() == 0)
                | (searchType == SEARCH_CONNECT_ALL_FOUND) )
                {  // return to main function
                    synchronized( block_c )
                    {
                        block_c.notifyAll();
                    }
                }
                // If deviceList is used, then it's ready now (will change "Stop"
                // button into "Cancel".
                if( deviceList != null )
                {
                    deviceList.ready();
                }
                return;
            }

            // search for the service
            try
            {
                currServRec = null;
                serviceSearchTransId = discoveryAgent.searchServices( attrbs, u, dev, listener );
            }
            catch( BluetoothStateException e )
            {
                // an error occured
                // Try next device
                nextDeviceServiceSearch();
            }
        }

       
        public void servicesDiscovered( int transID, ServiceRecord[] servRecord )
        {
            // A Service was found on the device.
            currServRec = servRecord[0];
        }

       
        public void serviceSearchCompleted( int transID, int respCode )
        {
            synchronized( block_ss )
            {
                // Reset trans action id
                serviceSearchTransId = -1;

                // Collect all devices with right service
                if( currServRec != null )
                {    // A device with this service is found, so add it
                    foundServiceRecords.addElement( currServRec );
                    urlStrings.addElement( currServRec.getConnectionURL( ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false ) );
                }

                switch( searchType )
                {
                    case SEARCH_CONNECT_FIRST_FOUND:
                        // Stop searching after first device with the service is found
                        if( currServRec != null )
                        {    // A device with this service is found, so stop
                            synchronized( block_c )
                            {
                                block_c.notifyAll();
                            }
                            return;
                        }
                        break;
                    case SEARCH_CONNECT_ALL_FOUND:
                        break;
                    case SEARCH_ALL_DEVICES_SELECT_ONE:
                    case SEARCH_ALL_DEVICES_SELECT_SEVERAL:
                        if( currServRec != null )
                        {   // Update displayed list
                            displayFoundServices();
                        }
                        break;
                }

                // Check if service search was terminated
                if( respCode == SERVICE_SEARCH_TERMINATED )
                {   // Notify the terminator (cancelServSearch)
                    synchronized( block_ss )
                    {
                        block_ss.notifyAll();
                    }
                }
                else
                {   // Search next device
                    nextDeviceServiceSearch();
                }
            }
        }

        /**
         * displayFoundServices
         * Updates the displayed found services.
         */
        private void displayFoundServices()
        {
            if( deviceList == null )
            {   // Create new device list
                int lt;

                if( searchType == SEARCH_ALL_DEVICES_SELECT_SEVERAL )
                {
                    lt = List.MULTIPLE;
                }
                else
                {
                    lt = List.IMPLICIT;
                }
                // Create new device lsit
                deviceList = new DeviceList( lt );
            }
            // Append new service/device
            // Retrieve remote name
            DataElement de = ((ServiceRecord)foundServiceRecords.lastElement()).getAttributeValue( SERVICE_NAME_BASE_LANGUAGE );
            String rname = (String) de.getValue();
            // Add device to list
            deviceList.append( rname, null );
        }
    }


   
    private class waitOnServSearchTermination
    extends Thread
    {
       
        public void run()
        {
            synchronized( block_ss )
            {
                if( serviceSearchTransId != -1 )
                {   // only if there is a service search active
                    discoveryAgent.cancelServiceSearch( serviceSearchTransId );
                    // wait until service search has terminated
                    try
                    {
                        block_ss.wait();
                    }
                    catch(InterruptedException e)
                    {
                        // We can ignore this
                    }
                    // Now notify searchService method
                    synchronized( block_c )
                    {
                        block_c.notifyAll();
                    }
                }
            }
        }
    }

    /**
     * DeviceList is a List to display the found devices.
     */

    private class DeviceList
    extends List
    implements CommandListener
    {
        // The possible commands
        private Command ok;
        private Command stop;
        private Command cancel;

        /** Constructor of DeviceList
         * @param list_type determines the type of list. Either MULTIPLE or IMPLICIT.
         */
        public DeviceList( int list_type )
        {
            super( "Select:", list_type );

            // Set text wrap around
            setFitPolicy( Choice.TEXT_WRAP_ON );

            // Create commands
            ok = new Command( "OK", Command.OK, 1 );
            stop = new Command( "Stop", Command.STOP, 1 );
            cancel = new Command( "Cancel", Command.CANCEL, 1 );
            // append commands
            addCommand( ok );
            addCommand( stop );
            setCommandListener( this );
            display.setCurrent( this );
        }

        /**
         * Cancels the current service search.
         */
        private void cancelServSearch()
        {
            synchronized( block_ss )
            {
                if( serviceSearchTransId != -1 )
                {   // only if there is a service search active
                    discoveryAgent.cancelServiceSearch( serviceSearchTransId );
                    // wait until service search has terminated
                    try
                    {
                        block_ss.wait();
                    }
                    catch( InterruptedException e )
                    {
                        // we can ignore this
                    }
                }
            }
        }

        /**
         * Should be called when no more elements will be added to the
         * list.
         * The "Stop" button will change into "Cancel".
         */
        public void ready()
        {
            removeCommand( stop );
            addCommand( cancel );
        }

        /**
         * Respond to commands.
         * @param c The command.
         * @param s The displayable object. */
        public void commandAction( Command c, Displayable s )
        {
            int com = c.getCommandType();

            if( (c == SELECT_COMMAND) && (searchType == SEARCH_ALL_DEVICES_SELECT_ONE) )
            {                // Behave the same as if OK was pressed.
                com = Command.OK;
            }

            switch( com )
            {
                case Command.OK:
                    // User selected OK
                    // Cancel the current service search.
                    cancelServSearch();

                    // Remove all elements that are not selected
                    for( int i=size()-1; i>=0; i-- )
                    {
                        if( ! isSelected(i) )
                        {   // not selected then remove
                            urlStrings.removeElementAt(i);
                        }
                    }

                    // return to searchService function
                    synchronized( block_c )
                    {
                        block_c.notifyAll();
                    }
                    break;

                case Command.STOP:
                    // User stopped
                    // Cancel the current service search.
                    cancelServSearch();
                    // Exchange stop button with cancel button
                    ready();
                    break;

                case Command.CANCEL:
                    // User cancelled
                    // Remove all elements
                    urlStrings.removeAllElements();
                    // return to main function
                    synchronized( block_c )
                    {
                        block_c.notifyAll();
                    }
                    break;
            }
        }
    }

    
    private class acceptAndOpenThread
    extends Thread
    {
        /**
         * run method
         * Start acceeptAndOpen and wait on Exception or connection.
         */
        public void run()
        {
            StreamConnection con;

            // Prepare data
            btConnections = new BluetoothConnection[1];
            // Register service
            try
            {
                // Wait on client
                con = (StreamConnection) notifier.acceptAndOpen();
                btConnections[0] = new BluetoothConnection(con, localName, "Host");
                // Read host name
                String remoteName = btConnections[0].readString();
                btConnections[0].setRemoteName( remoteName );
            }
            catch(Exception e)
            {
                // Accept and open terminated abnormally (maybe cancel)
                btConnections[0] = null;
            }
            // Remove notifier
            closeNotifier();
            // wakeup
            synchronized( block_s )
            {
                block_s.notifyAll();
            }
        }
    }
}
