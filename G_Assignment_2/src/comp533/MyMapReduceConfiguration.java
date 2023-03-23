package comp533;

import comp533.barrier.TokenCounterBarrier;
import comp533.controller.TokenCounterController;
import comp533.joiner.TokenCounterJoiner;
import comp533.model.SumModel;
import comp533.model.TokenCounterModel;
import comp533.partitioner.TokenCounterPartitioner;
import comp533.partitioner.TokenCounterPartitionerFactory;
import comp533.reducer.Reducer;
import comp533.reducer.ReducerFactory;
import comp533.slave.Slave;
import comp533.view.TokenCounterView;
import gradingTools.comp533s21.assignment1.interfaces.MapReduceConfiguration;
import comp533.keyValue.KeyValue;
import comp533.mapper.Mapper;
import comp533.mapper.MapperFactory;
import comp533.mapper.SumMapper;

public class MyMapReduceConfiguration implements MapReduceConfiguration {

	public Class<?> getSlaveClass() {
		// TODO Auto-generated method stub
		return Slave.class;
	}
	
	public Object getBarrier(final int aNumThreads) {
		// TODO Auto-generated method stub
		return new TokenCounterBarrier(aNumThreads);
	}

	public Class<?> getBarrierClass() {
		// TODO Auto-generated method stub
		return TokenCounterBarrier.class;
	}

	public Class<?> getClientTokenCounter() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getControllerClass() {
		return TokenCounterController.class;
	}

	public Object getIntSummingMapper() {
//		return SumModel.getSumMapper();
		return null;
	}

	public Class<?> getIntSummingMapperClass() {
		return SumMapper.class;
	}

	public Object getJoiner(final int aNumThreads) {
		// TODO Auto-generated method stub
		return new TokenCounterJoiner(aNumThreads);
	}

	public Class<?> getJoinerClass() {
		// TODO Auto-generated method stub
		 return TokenCounterJoiner.class;
	}

	public Class<?> getKeyValueClass() {
		return KeyValue.class;
	}

	public Class<?> getMapperFactory() {
		return MapperFactory.class;
	}

	public Class<?> getModelClass() {
		return TokenCounterModel.class;
	}

	public Object getPartitioner() {
		// TODO Auto-generated method stub
		return TokenCounterPartitionerFactory.getPartitioner();
	}

	public Class<?> getPartitionerClass() {
		// TODO Auto-generated method stub
		return TokenCounterPartitioner.class;
	}

	public Class<?> getPartitionerFactory() {
		// TODO Auto-generated method stub
		return TokenCounterPartitionerFactory.class;
	}

	public Object getReducer() {
		return ReducerFactory.getReducer();
	}

	public Class<?> getReducerClass() {
		return Reducer.class;
	}

	public Class<?> getReducerFactory() {
		return ReducerFactory.class;
	}

	public Class<?> getRemoteClientFacebookMapReduce() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getRemoteClientObjectClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getRemoteClientObjectInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getRemoteModelInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getServerFacebookMapReduce() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getServerIntegerSummer() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getServerTokenCounter() {
		// TODO Auto-generated method stub
		return null;
	}



	public Class<?> getStandAloneFacebookMapReduce() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getStandAloneIntegerSummer() {
		return SumMain.class;
	}

	public Class<?> getStandAloneTokenCounter() {
		return MvcTokenCounter.class;
	}

	public Object getTokenCountingMapper() {
		return MapperFactory.getMapper();
	}

	public Class<?> getTokenCountingMapperClass() {
		return Mapper.class;
	}

	public Class<?> getViewClass() {
		return TokenCounterView.class;
	}

}
