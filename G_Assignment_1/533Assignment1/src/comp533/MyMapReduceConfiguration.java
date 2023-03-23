package comp533;

import comp533.controller.TokenCounterController;
import comp533.model.SumModel;
import comp533.model.TokenCounterModel;
import comp533.reducer.Reducer;
import comp533.reducer.ReducerFactory;
import comp533.view.TokenCounterView;
import gradingTools.comp533s21.assignment1.interfaces.MapReduceConfiguration;
import comp533.keyValue.KeyValue;
import comp533.mapper.Mapper;
import comp533.mapper.MapperFactory;
import comp533.mapper.SumMapper;

public class MyMapReduceConfiguration implements MapReduceConfiguration {

	public Object getBarrier(final int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getBarrierClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getClientTokenCounter() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getControllerClass() {
		return TokenCounterController.class;
	}

	public Object getIntSummingMapper() {
		return SumModel.getSumMapper();
	}

	public Class<?> getIntSummingMapperClass() {
		return SumMapper.class;
	}

	public Object getJoiner(final int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getJoinerClass() {
		// TODO Auto-generated method stub
		return null;
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
		return null;
	}

	public Class<?> getPartitionerClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getPartitionerFactory() {
		// TODO Auto-generated method stub
		return null;
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

	public Class<?> getSlaveClass() {
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
