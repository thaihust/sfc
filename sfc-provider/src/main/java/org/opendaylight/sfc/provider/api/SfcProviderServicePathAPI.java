/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.sfc.provider.api;

import com.google.common.base.Optional;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.ServiceFunctionsState;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.service.functions.ServiceFunction;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.service.functions.state.ServiceFunctionState;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.service.functions.state.ServiceFunctionStateKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfc.rev140701.ServiceFunctionChainsState;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfc.rev140701.service.function.chain.grouping.ServiceFunctionChain;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfc.rev140701.service.function.chain.grouping.service.function.chain.SfcServiceFunction;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfc.rev140701.service.function.chains.state.ServiceFunctionChainState;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfc.rev140701.service.function.chains.state.ServiceFunctionChainStateBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfc.rev140701.service.function.chains.state.ServiceFunctionChainStateKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfp.rev140701.ServiceFunctionPaths;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfp.rev140701.service.function.paths.ServiceFunctionPath;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfp.rev140701.service.function.paths.ServiceFunctionPathBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfp.rev140701.service.function.paths.ServiceFunctionPathKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfp.rev140701.service.function.paths.service.function.path.ServicePathHop;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfp.rev140701.service.function.paths.service.function.path.ServicePathHopBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sft.rev140701.service.function.types.ServiceFunctionType;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sft.rev140701.service.function.types.service.function.type.SftServiceFunctionName;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class has the APIs to operate on the ServiceFunctionPath
 * datastore.
 * <p/>
 * It is normally called from onDataChanged() through a executor
 * service. We need to use an executor service because we can not
 * operate on a datastore while on onDataChanged() context.
 *
 * @author Reinaldo Penno (rapenno@gmail.com)
 * @author Konstantin Blagov (blagov.sk@hotmail.com)
 * @version 0.1
 * @see org.opendaylight.sfc.provider.SfcProviderSfpEntryDataListener
 * <p/>
 * <p/>
 * <p/>
 * @since       2014-06-30
 */
public class SfcProviderServicePathAPI extends SfcProviderAbstractAPI {

    private static final Logger LOG = LoggerFactory.getLogger(SfcProviderServicePathAPI.class);
    private static AtomicInteger numCreatedPath = new AtomicInteger(0);

    SfcProviderServicePathAPI(Object[] params, Class[] paramsTypes, String m) {
        super(params, paramsTypes, m);
    }

    @SuppressWarnings("unused")
    public static SfcProviderServicePathAPI getPut(Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "putServiceFunctionPath");
    }
    @SuppressWarnings("unused")
    public static SfcProviderServicePathAPI getRead(Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "readServiceFunctionPath");
    }
    @SuppressWarnings("unused")
    public static SfcProviderServicePathAPI getDelete(Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "deleteServiceFunctionPath");
    }
    @SuppressWarnings("unused")
    public static SfcProviderServicePathAPI getPutAll(Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "putAllServiceFunctionPaths");
    }
    @SuppressWarnings("unused")
    public static SfcProviderServicePathAPI getReadAll(Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "readAllServiceFunctionPaths");
    }
    @SuppressWarnings("unused")
    public static SfcProviderServicePathAPI getDeleteAll(Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "deleteAllServiceFunctionPaths");
    }

    public static  SfcProviderServicePathAPI getDeleteServicePathContainingFunction (Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "deleteServicePathContainingFunction");
    }
    @SuppressWarnings("unused")
    public static  SfcProviderServicePathAPI getDeleteServicePathInstantiatedFromChain (Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "deleteServicePathInstantiatedFromChain");
    }

    public static  SfcProviderServicePathAPI getCreateServicePathAPI(Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "createServiceFunctionPathEntry");
    }
    @SuppressWarnings("unused")
    public static  SfcProviderServicePathAPI getUpdateServicePathAPI(Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "updateServiceFunctionPathEntry");
    }
    @SuppressWarnings("unused")
    public static  SfcProviderServicePathAPI getUpdateServicePathInstantiatedFromChain(Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "updateServicePathInstantiatedFromChain");
    }

    public static  SfcProviderServicePathAPI getUpdateServicePathContainingFunction(Object[] params, Class[] paramsTypes) {
        return new SfcProviderServicePathAPI(params, paramsTypes, "updateServicePathContainingFunction");
    }
    @SuppressWarnings("unused")
    public static int numCreatedPathGetValue() {
        return numCreatedPath.get();
    }

    public int numCreatedPathIncrementGet() {
        return numCreatedPath.incrementAndGet();
    }
    @SuppressWarnings("unused")
    public int numCreatedPathDecrementGet() {
        return numCreatedPath.decrementAndGet();
    }

    @SuppressWarnings("unused")
    protected boolean putServiceFunctionPath(ServiceFunctionPath sfp) {
        boolean ret = false;
        LOG.debug("\n####### Start: {}", Thread.currentThread().getStackTrace()[1]);
        if (dataBroker != null) {

            InstanceIdentifier<ServiceFunctionPath> sfpEntryIID = InstanceIdentifier.builder(ServiceFunctionPaths.class).
                    child(ServiceFunctionPath.class, sfp.getKey()).toInstance();

            WriteTransaction writeTx = dataBroker.newWriteOnlyTransaction();
            writeTx.merge(LogicalDatastoreType.CONFIGURATION,
                    sfpEntryIID, sfp, true);
            writeTx.commit();

            ret = true;
        }
        LOG.debug("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);
        return ret;
    }

    protected ServiceFunctionPath readServiceFunctionPath(String serviceFunctionPathName) {
        LOG.debug("\n####### Start: {}", Thread.currentThread().getStackTrace()[1]);
        ServiceFunctionPath sfp = null;
        InstanceIdentifier<ServiceFunctionPath> sfpIID;
        ServiceFunctionPathKey serviceFunctionPathKey = new ServiceFunctionPathKey(serviceFunctionPathName);
        sfpIID = InstanceIdentifier.builder(ServiceFunctionPaths.class)
                .child(ServiceFunctionPath.class, serviceFunctionPathKey).build();

        if (dataBroker != null) {
            ReadOnlyTransaction readTx = dataBroker.newReadOnlyTransaction();
            Optional<ServiceFunctionPath> serviceFunctionPathDataObject = null;
            try {
                serviceFunctionPathDataObject = readTx.read(LogicalDatastoreType.CONFIGURATION, sfpIID).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (serviceFunctionPathDataObject != null
                    && serviceFunctionPathDataObject.isPresent()) {
                sfp = serviceFunctionPathDataObject.get();
            }
        }
        LOG.debug("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);
        return sfp;
    }

    protected boolean deleteServiceFunctionPath(String serviceFunctionPathName) {
        boolean ret = false;
        LOG.debug("\n####### Start: {}", Thread.currentThread().getStackTrace()[1]);
        ServiceFunctionPathKey serviceFunctionPathKey = new ServiceFunctionPathKey(serviceFunctionPathName);
        InstanceIdentifier<ServiceFunctionPath> sfpEntryIID = InstanceIdentifier.builder(ServiceFunctionPaths.class)
                .child(ServiceFunctionPath.class, serviceFunctionPathKey).toInstance();

        if (dataBroker != null) {
            WriteTransaction writeTx = dataBroker.newWriteOnlyTransaction();
            writeTx.delete(LogicalDatastoreType.CONFIGURATION, sfpEntryIID);
            writeTx.commit();

            ret = true;
        }
        LOG.debug("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);
        return ret;
    }

    protected boolean putAllServiceFunctionPaths(ServiceFunctionPaths sfps) {
        boolean ret = false;
        LOG.debug("\n####### Start: {}", Thread.currentThread().getStackTrace()[1]);
        if (dataBroker != null) {

            InstanceIdentifier<ServiceFunctionPaths> sfpsIID = InstanceIdentifier.builder(ServiceFunctionPaths.class).toInstance();

            WriteTransaction writeTx = dataBroker.newWriteOnlyTransaction();
            writeTx.merge(LogicalDatastoreType.CONFIGURATION, sfpsIID, sfps);
            writeTx.commit();

            ret = true;
        }
        LOG.debug("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);
        return ret;
    }

    protected ServiceFunctionPaths readAllServiceFunctionPaths() {
        ServiceFunctionPaths sfps = null;
        LOG.debug("\n####### Start: {}", Thread.currentThread().getStackTrace()[1]);
        InstanceIdentifier<ServiceFunctionPaths> sfpsIID = InstanceIdentifier.builder(ServiceFunctionPaths.class).toInstance();

        if (odlSfc.getDataProvider() != null) {
            ReadOnlyTransaction readTx = odlSfc.getDataProvider().newReadOnlyTransaction();
            Optional<ServiceFunctionPaths> serviceFunctionPathsDataObject = null;
            try {
                serviceFunctionPathsDataObject = readTx.read(LogicalDatastoreType.CONFIGURATION, sfpsIID).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (serviceFunctionPathsDataObject != null
                    && serviceFunctionPathsDataObject.isPresent()) {
                sfps = serviceFunctionPathsDataObject.get();
            }
        }
        LOG.debug("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);
        return sfps;
    }

    protected boolean deleteAllServiceFunctionPaths() {
        boolean ret = false;
        LOG.debug("\n####### Start: {}", Thread.currentThread().getStackTrace()[1]);
        if (odlSfc.getDataProvider() != null) {

            InstanceIdentifier<ServiceFunctionPaths> sfpsIID = InstanceIdentifier.builder(ServiceFunctionPaths.class).toInstance();

            WriteTransaction writeTx = odlSfc.getDataProvider().newWriteOnlyTransaction();
            writeTx.delete(LogicalDatastoreType.CONFIGURATION, sfpsIID);
            writeTx.commit();

            ret = true;
        }
        LOG.debug("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);
        return ret;
    }

    /* Today A Service Function Chain modification is catastrophic. We delete all Paths
     * and recreate them. Maybe a real patch is possible but given the complexities of the possible
     * modifications, this is the safest approach.
     */
    @SuppressWarnings("unused")
    private void updateServicePathInstantiatedFromChain (ServiceFunctionPath serviceFunctionPath) {
        deleteServicePathInstantiatedFromChain(serviceFunctionPath);
        createServiceFunctionPathEntry(serviceFunctionPath);
    }

    // TODO:Needs change
    private void deleteServicePathInstantiatedFromChain (ServiceFunctionPath serviceFunctionPath) {

        LOG.debug("\n####### Start: {}", Thread.currentThread().getStackTrace()[1]);
        ServiceFunctionChain serviceFunctionChain = null;
        String serviceChainName = serviceFunctionPath.getServiceChainName();
        try {
            serviceFunctionChain = serviceChainName != null ?
                    (ServiceFunctionChain) odlSfc.executor
                            .submit(SfcProviderServiceChainAPI.getRead(
                                    new Object[]{serviceChainName},
                                    new Class[]{String.class})).get()
                    : null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (serviceFunctionChain == null) {
            LOG.error("\n########## ServiceFunctionChain name for Path {} not provided",
                    serviceFunctionPath.getName());
            return;
        }


        InstanceIdentifier<ServiceFunctionPath> sfpIID;
        ServiceFunctionChainState serviceFunctionChainState;
        ServiceFunctionChainStateKey serviceFunctionChainStateKey =
                new ServiceFunctionChainStateKey(serviceFunctionChain.getName());
        InstanceIdentifier<ServiceFunctionChainState> sfcStateIID =
                InstanceIdentifier.builder(ServiceFunctionChainsState.class)
                        .child(ServiceFunctionChainState.class, serviceFunctionChainStateKey)
                        .build();

        ReadOnlyTransaction readTx = odlSfc.getDataProvider().newReadOnlyTransaction();
        Optional<ServiceFunctionChainState> serviceFunctionChainStateObject = null;
        try {
            serviceFunctionChainStateObject = readTx.read(LogicalDatastoreType.OPERATIONAL, sfcStateIID).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        // TODO: Remove path name from Service Function path list
        if (serviceFunctionChainStateObject instanceof ServiceFunctionChainState) {
            serviceFunctionChainState = (ServiceFunctionChainState) serviceFunctionChainStateObject;
            List<String> sfcServiceFunctionPathList =
                    serviceFunctionChainState.getSfcServiceFunctionPath();
            List<String> removedPaths = new ArrayList<>();
            for (String pathName : sfcServiceFunctionPathList) {

                ServiceFunctionPathKey serviceFunctionPathKey = new ServiceFunctionPathKey(pathName);
                sfpIID = InstanceIdentifier.builder(ServiceFunctionPaths.class)
                        .child(ServiceFunctionPath.class, serviceFunctionPathKey)
                        .build();

                WriteTransaction writeTx = odlSfc.getDataProvider().newWriteOnlyTransaction();
                writeTx.delete(LogicalDatastoreType.CONFIGURATION,
                        sfpIID);
                writeTx.commit();

            }

            sfcServiceFunctionPathList.removeAll(removedPaths);

            /* After we are done removing all paths from the datastore we commit the updated the path list
             * under the Service Chain operational tree
             */
            ServiceFunctionChainStateBuilder serviceFunctionChainStateBuilder  = new ServiceFunctionChainStateBuilder();
            serviceFunctionChainStateBuilder.setName(serviceFunctionChain.getName());
            serviceFunctionChainStateBuilder.setSfcServiceFunctionPath(sfcServiceFunctionPathList);
            WriteTransaction writeTx = odlSfc.getDataProvider().newWriteOnlyTransaction();
            writeTx.merge(LogicalDatastoreType.OPERATIONAL,
                    sfcStateIID, serviceFunctionChainStateBuilder.build(), true);
            writeTx.commit();

        } else {
            LOG.error("Failed to get reference to Service Function Chain State {} ", serviceFunctionChain.getName());
        }
        LOG.debug("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);
    }

    @SuppressWarnings("unused")
    private void updateServiceFunctionPathEntry (ServiceFunctionPath serviceFunctionPath) {
        this.createServiceFunctionPathEntry(serviceFunctionPath);
    }

    /*
     * This function is actually an updated to a previously created SFP where only
     * the service chain name was given. In this function we patch the SFP with the
     * names of the chosen SFs
     */
    protected void createServiceFunctionPathEntry (ServiceFunctionPath serviceFunctionPath) {

        LOG.debug("\n####### Start: {}", Thread.currentThread().getStackTrace()[1]);

        long pathId;
        short pos_index = 0;
        int service_index;
        ServiceFunctionChain serviceFunctionChain;
        serviceFunctionChain = null;
        String serviceFunctionChainName = serviceFunctionPath.getServiceChainName();
        try {
            serviceFunctionChain = serviceFunctionChainName != null ?
                    (ServiceFunctionChain) odlSfc.executor
                            .submit(SfcProviderServiceChainAPI.getRead(
                                    new Object[]{serviceFunctionChainName},
                                    new Class[]{String.class})).get()
                    : null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (serviceFunctionChain == null) {
            LOG.error("\n########## ServiceFunctionChain name for Path {} not provided",
                    serviceFunctionPath.getName());
            return;
        }


        ServiceFunctionPathBuilder serviceFunctionPathBuilder = new ServiceFunctionPathBuilder();
        ArrayList<ServicePathHop> servicePathHopArrayList = new ArrayList<>();
        ServicePathHopBuilder servicePathHopBuilder = new ServicePathHopBuilder();

        /*
         * For each ServiceFunction type in the list of ServiceFunctions we select a specific
         * service function from the list of service functions by type.
         */
        List<SfcServiceFunction> SfcServiceFunctionList = serviceFunctionChain.getSfcServiceFunction();
        service_index = SfcServiceFunctionList.size();
        for (SfcServiceFunction sfcServiceFunction : SfcServiceFunctionList) {
            LOG.debug("\n########## ServiceFunction name: {}", sfcServiceFunction.getName());

            /*
             * We iterate thorough the list of service function types and for each one we try to get
             * get a suitable Service Function. WE need to perform lots of checking to make sure
             * we do not hit NULL Pointer exceptions
             */

            ServiceFunctionType serviceFunctionType = null;
            try {
                serviceFunctionType = (ServiceFunctionType) odlSfc.executor.submit(SfcProviderServiceTypeAPI.getRead(
                        new Object[]{sfcServiceFunction.getType()}, new Class[]{String.class})).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (serviceFunctionType != null) {
                List<SftServiceFunctionName> sftServiceFunctionNameList = serviceFunctionType.getSftServiceFunctionName();
                if (!sftServiceFunctionNameList.isEmpty()) {
                    for (SftServiceFunctionName sftServiceFunctionName : sftServiceFunctionNameList) {
                        // TODO: API to select suitable Service Function
                        String serviceFunctionName = sftServiceFunctionName.getName();
                        ServiceFunction serviceFunction = null;
                        try {
                            serviceFunction =
                                    (ServiceFunction) odlSfc.executor.submit(SfcProviderServiceFunctionAPI
                                            .getRead(new Object[]{serviceFunctionName}, new Class[]{String.class})).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        if (serviceFunction != null) {
                            servicePathHopBuilder.setHopNumber(pos_index)
                                    .setServiceFunctionName(serviceFunctionName)
                                    .setServiceIndex((short) service_index)
                                    .setServiceFunctionForwarder(serviceFunction.getSfDataPlaneLocator()
                                            .get(0).getServiceFunctionForwarder());
                            servicePathHopArrayList.add(pos_index, servicePathHopBuilder.build());
                            service_index--;
                            pos_index++;
                            break;
                        } else {
                            LOG.error("\n####### Could not find suitable SF of type in data store: {}",
                                    sfcServiceFunction.getType());
                            return;
                        }
                    }
                } else {
                    LOG.error("\n########## No configured SFs of type: {}", sfcServiceFunction.getType());
                    return;
                }
            } else {
                LOG.error("\n########## No configured SFs of type: {}", sfcServiceFunction.getType());
                return;
            }

        }

        //Build the service function path so it can be committed to datastore


        pathId = (serviceFunctionPath.getPathId() != null)  ?  serviceFunctionPath.getPathId()
                : numCreatedPathIncrementGet();
        serviceFunctionPathBuilder.setServicePathHop(servicePathHopArrayList);
        if (serviceFunctionPath.getName().isEmpty())  {
            serviceFunctionPathBuilder.setName(serviceFunctionChainName + "-Path-" + pathId);
        } else {
            serviceFunctionPathBuilder.setName(serviceFunctionPath.getName());

        }

        serviceFunctionPathBuilder.setPathId(pathId);
        // TODO: Find out the exact rules for service index generation
        serviceFunctionPathBuilder.setStartingIndex((short) servicePathHopArrayList.size());
        serviceFunctionPathBuilder.setServiceChainName(serviceFunctionChainName);

        ServiceFunctionPathKey serviceFunctionPathKey = new
                ServiceFunctionPathKey(serviceFunctionPathBuilder.getName());
        InstanceIdentifier<ServiceFunctionPath> sfpIID;
        sfpIID = InstanceIdentifier.builder(ServiceFunctionPaths.class)
                .child(ServiceFunctionPath.class, serviceFunctionPathKey)
                .build();

        ServiceFunctionPath newServiceFunctionPath = serviceFunctionPathBuilder.build();
        WriteTransaction writeTx = odlSfc.getDataProvider().newWriteOnlyTransaction();
        writeTx.put(LogicalDatastoreType.CONFIGURATION,
                sfpIID, newServiceFunctionPath, true);
        writeTx.commit();
        //SfcProviderServiceForwarderAPI.addPathIdtoServiceFunctionForwarder(newServiceFunctionPath);
        SfcProviderServiceFunctionAPI.addPathToServiceFunctionState(newServiceFunctionPath);

        LOG.debug("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);

    }

    /*
    private void deleteServiceFunctionPathEntry (ServiceFunctionChain serviceFunctionChain) {

        LOG.info("\n########## Start: {}", Thread.currentThread().getStackTrace()[1]);
        String serviceFunctionChainName = serviceFunctionChain.getName();
        ServiceFunctionPathKey serviceFunctionPathKey = new ServiceFunctionPathKey(serviceFunctionChainName + "-Path");
        InstanceIdentifier<ServiceFunctionPath> sfpIID;
        sfpIID = InstanceIdentifier.builder(ServiceFunctionPaths.class)
                .child(ServiceFunctionPath.class, serviceFunctionPathKey)
                .build();

        WriteTransaction writeTx = odlSfc.dataProvider.newWriteOnlyTransaction();
        writeTx.delete(LogicalDatastoreType.CONFIGURATION,
                sfpIID);
        writeTx.commit();
        LOG.info("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);

    }
    */


    /*
     * We iterate through all service paths that use this service function and remove them.
     * In the end since there is no more operational state, we remove the state tree.
     */

    @SuppressWarnings("unused")
    private void deleteServicePathContainingFunction (ServiceFunction serviceFunction) {

        LOG.debug("\n####### Start: {}", Thread.currentThread().getStackTrace()[1]);

        InstanceIdentifier<ServiceFunctionPath> sfpIID;
        ServiceFunctionState serviceFunctionState;
        ServiceFunctionStateKey serviceFunctionStateKey =
                new ServiceFunctionStateKey(serviceFunction.getName());
        InstanceIdentifier<ServiceFunctionState> sfStateIID =
                InstanceIdentifier.builder(ServiceFunctionsState.class)
                        .child(ServiceFunctionState.class, serviceFunctionStateKey)
                        .build();

        ReadOnlyTransaction readTx = odlSfc.getDataProvider().newReadOnlyTransaction();
        Optional<ServiceFunctionState> serviceFunctionStateObject = null;
        try {
            serviceFunctionStateObject = readTx.read(LogicalDatastoreType.OPERATIONAL, sfStateIID).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if ((serviceFunctionStateObject != null) &&
                (serviceFunctionStateObject.get() instanceof ServiceFunctionState)) {
            serviceFunctionState = serviceFunctionStateObject.get();
            List<String> sfServiceFunctionPathList =
                    serviceFunctionState.getSfServiceFunctionPath();
            List<String> removedPaths = new ArrayList<>();
            for (String pathName : sfServiceFunctionPathList) {

                ServiceFunctionPathKey serviceFunctionPathKey = new ServiceFunctionPathKey(pathName);
                sfpIID = InstanceIdentifier.builder(ServiceFunctionPaths.class)
                        .child(ServiceFunctionPath.class, serviceFunctionPathKey)
                        .build();

                WriteTransaction writeTx = odlSfc.getDataProvider().newWriteOnlyTransaction();
                writeTx.delete(LogicalDatastoreType.CONFIGURATION,
                        sfpIID);
                writeTx.commit();
                // TODO: Need to consider failure of transaction
                removedPaths.add(pathName);
            }

            // If no more SFP associated with this SF, remove the state.
            if (removedPaths.containsAll(sfServiceFunctionPathList)) {
                SfcProviderServiceFunctionAPI.deleteServiceFunctionState(serviceFunction.getName());
            } else {
                LOG.error("Could not remove all paths containing function: {} ", serviceFunction.getName());
            }
        } else {
            LOG.warn("Failed to get reference to Service Function State {} ", serviceFunction.getName());
        }
        LOG.debug("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);
    }


    /*
     * When a SF is updated, meaning key remains the same, but other fields change we need to
     * update all affected SFPs. We need to do that because admin can update critical fields
     * as SFC type, rendering the path unfeasible. The update reads the current path from
     * data store, keeps pathID intact and rebuild the SF list.
     *
     * The update can or not work.
     */
    private void updateServicePathContainingFunction (ServiceFunction serviceFunction) {

        LOG.debug("\n####### Start: {}", Thread.currentThread().getStackTrace()[1]);

        InstanceIdentifier<ServiceFunctionPath> sfpIID;

        ServiceFunctionState serviceFunctionState = SfcProviderServiceFunctionAPI.readServiceFunctionState(serviceFunction.getName());
        if (serviceFunctionState != null) {
            List<String> sfServiceFunctionPathList =
                    serviceFunctionState.getSfServiceFunctionPath();
            for (String pathName : sfServiceFunctionPathList) {

                ServiceFunctionPathKey serviceFunctionPathKey = new ServiceFunctionPathKey(pathName);
                sfpIID = InstanceIdentifier.builder(ServiceFunctionPaths.class)
                        .child(ServiceFunctionPath.class, serviceFunctionPathKey)
                        .build();

                ReadOnlyTransaction readTx = odlSfc.getDataProvider().newReadOnlyTransaction();
                Optional<ServiceFunctionPath> serviceFunctionPathObject = null;
                try {
                    serviceFunctionPathObject = readTx.read(LogicalDatastoreType.CONFIGURATION, sfpIID).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                if (serviceFunctionPathObject != null &&
                        (serviceFunctionPathObject.get() instanceof  ServiceFunctionPath)) {
                    ServiceFunctionPath servicefunctionPath = serviceFunctionPathObject.get();
                    createServiceFunctionPathEntry(servicefunctionPath);
                }
            }
        } else {
            LOG.error("Failed to get reference to Service Function State {} ", serviceFunction.getName());
        }
        LOG.debug("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);
    }
}
