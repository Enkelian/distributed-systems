// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: src/main/resources/proto/calculator.proto

package sr.grpc.gen;

public interface ComplexArithmeticOpArgumentsOrBuilder extends
    // @@protoc_insertion_point(interface_extends:calculator.ComplexArithmeticOpArguments)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.calculator.OperationType optype = 1;</code>
   */
  int getOptypeValue();
  /**
   * <code>.calculator.OperationType optype = 1;</code>
   */
  sr.grpc.gen.OperationType getOptype();

  /**
   * <code>repeated double args = 2;</code>
   */
  java.util.List<java.lang.Double> getArgsList();
  /**
   * <code>repeated double args = 2;</code>
   */
  int getArgsCount();
  /**
   * <code>repeated double args = 2;</code>
   */
  double getArgs(int index);
}